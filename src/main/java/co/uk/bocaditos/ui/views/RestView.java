package co.uk.bocaditos.ui.views;

import java.lang.reflect.InvocationTargetException;
import java.text.MessageFormat;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.util.ObjectUtils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.vaadin.flow.component.ClickEvent;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.select.Select;
import com.vaadin.flow.component.tabs.Tab;
import com.vaadin.flow.component.tabs.Tabs;
import com.vaadin.flow.data.binder.BinderValidationStatus;
import com.vaadin.flow.data.binder.ValidationResult;
import com.vaadin.flow.data.binder.ValueContext;
import com.vaadin.flow.data.binder.Binder.BindingBuilder;
import com.vaadin.flow.data.validator.AbstractValidator;

import co.uk.bocaditos.ui.data.entity.JsonComparator.ErrorMsg;
import co.uk.bocaditos.ui.data.service.ObjService;


/**
 * Rest view.
 * 
 * @author aasco
 *
 * @param <Q> the request bean type.
 * @param <P> the response bean type.
 */
@SuppressWarnings("serial")
public class RestView<Q, P> extends VerticalLayout implements ResponseListener<P>, 
		ValidationErrorListener, TabsWithMain, ComponentEventListener<ClickEvent<Button>>, 
		RestInfo<Q>, SelectListener, ValidChangeListener {

	private static final Logger logger = LoggerFactory.getLogger(RestView.class);
	
	private static final String MENU_SUBMIT = "view.menu.submit";
	private static final String MENU_CLEAN  = "view.menu.clean";
	private static final String MENU_UPLOAD = "view.menu.upload";
	
	// Default
	private static final String DEFAULT_LBL_SUBMIT = "Submit";
	private static final String DEFAULT_LBL_CLEAN  = "Clean";
	private static final String DEFAULT_LBL_UPLOAD = "Upload";
	
	// Button indexes
	private static final int INDEX_HEADERS  	= 0;
	private static final int INDEX_URL_PARAMS	= 1;
	private static final int INDEX_REQUEST		= 2;
	static final int INDEX_RESPONSE 			= 3;
	static final int INDEX_ERRORS   			= 4;
	
	private static final String WIDTH = "30px";
	private static final String ITEM_WIDTH = "26px";
	
	private static final String[] baseMenuItemIds = {MENU_SUBMIT, MENU_CLEAN, MENU_UPLOAD};
	private static final boolean[][] menuItemsEnabled = {
			{true, true, true}, {false, true, true}, {false, false, false}};

    private final transient EntryPointWrapper entryPointSupport;
	private VerticalLayout menu;
	private Button[] buttons;
    private Tabs tabs;
    // Views
    private final MapView headersView;
    private final MapView urlParamsView;
    private final ObjView<Q, P> requestView;
    private ResponseView<P> responseView;
    private final ValidationView validationView;
	private Boolean valid;
    
    @Autowired
    private ObjectMapper mapper;


    public RestView(final Class<Q> clazz, final ObjService<Q, P> objService, final Environment env) 
    		throws SecurityException, NoSuchMethodException {
    	final String name = getClass().getSimpleName()
    			.substring(0, getClass().getSimpleName().length() - "View".length()).toLowerCase();

    	getStyle().set(EntryPointWrapper.WIDTH, "100%");
    	getStyle().set(EntryPointWrapper.HEIGHT, "100%");
    	getStyle().set("padding", "8px");
    	getStyle().set("padding-top", "0px");
		this.entryPointSupport = new EntryPointWrapper(this);
//		this.headersView = new MapView(name + ".headers", env);
		this.headersView = new MapView(name + ".headers", this);
//		this.urlParamsView = new MapView(name + ".urlparams", env);
		this.urlParamsView = new MapView(name + ".urlparams", this);
    	this.requestView = new ObjView<>(name, clazz, objService, this, this, this);
        this.validationView = new ValidationView(this);
        this.responseView = new ResponseView<>(ObjView.PROP_PRE + name + ".response.", this, this);
        init(name, env);
        clearForm();
    }

	@Override
	public void update(final Path path) {
		this.headersView.update(path.getRequestName(), path.getHeaderParams());
		((Tab) this.tabs.getComponentAt(INDEX_HEADERS)).setEnabled(this.headersView.exists());
		this.urlParamsView.update(path.getRequestName(), path.getUrlParams());
		((Tab) this.tabs.getComponentAt(INDEX_URL_PARAMS)).setEnabled(this.urlParamsView.exists());
		this.requestView.update(path.getRequestClass());
		this.responseView.update(path.getResponseClass());
	}
	
	@Override
	public void valid(final String name, boolean valid) {
		valid = (this.headersView == null || this.headersView.isValid()) 
				&& (this.urlParamsView == null || this.urlParamsView.isValid()) 
				&& this.requestView.isValid();
		if (this.valid == null || this.valid != valid) {
			this.valid = valid;
			this.requestView.setSubmitEnabled(this.valid);
		}
	}
    
    @Override
    public void setMainTab(final int tabIndex) {
    	if (tabIndex > INDEX_REQUEST) {
    		 ((Tab) this.tabs.getComponentAt(tabIndex)).setEnabled(false);
    	}
    	this.tabs.setSelectedIndex(INDEX_REQUEST);
    }

	@Override
	public void change(final P response) {
		int index = INDEX_RESPONSE;
		String responseTxt = "";

        ((Tab) this.tabs.getComponentAt(INDEX_ERRORS)).setEnabled(false);
		this.validationView.clear();
		if (response != null) {
			try {
				responseTxt = this.mapper.writerWithDefaultPrettyPrinter()
						.writeValueAsString(response);
			} catch (final JsonProcessingException jpe) {
				logger.warn("Failed to convert response to JSON: " + jpe.getMessage(), jpe);
				index = INDEX_REQUEST;
			}
		}
		this.responseView.setValue(responseTxt);
		((Tab) this.tabs.getComponentAt(INDEX_RESPONSE)).setEnabled(index == INDEX_RESPONSE);
		this.tabs.setSelectedIndex(index);
	}
	
	@Override
	public void clearForm() {
		this.headersView.clear();
		this.urlParamsView.clear();
		this.requestView.clear();
		this.responseView.clear();
		this.validationView.clear();
	}

	@Override
	public void processErrors(final List<ErrorMsg> errors) {
		final StringBuilder msgs = new StringBuilder();

		for (final ErrorMsg error : errors) {
			if (msgs.length() > 0) {
				msgs.append('\n');
			}
			msgs.append('[')
				.append(error.getPath())
				.append("] ")
				.append(error.getError());
		}
		this.validationView.setValue(msgs.toString());
        ((Tab) this.tabs.getComponentAt(INDEX_ERRORS)).setEnabled(true);
		this.tabs.setSelectedIndex(INDEX_ERRORS);
	}
	
	// RestInfo
	@Override
	public String getMethod() {
		return this.entryPointSupport.getMethod();
	}

	@Override
	public String getUri() {
		return this.entryPointSupport.getUri();
	}

	@Override
	public Map<String, String> getUrlParams() {
		return this.urlParamsView.getData();
	}

	@Override
	public Map<String, String> getHeaders() {
		return this.headersView.getData();
	}

	@Override
	public Q getRequest() {
		return this.requestView.getBean();
	}

	@Override
	public void setNewBean() {
		this.entryPointSupport.setNewBean();
	}

	@Override
	public BinderValidationStatus<EntryPoint> validate() {
		return this.entryPointSupport.validate();
	}
	// End RestInfo

    private void init(final String name, final Environment env) {
    	final Component[] components = {this.headersView, this.urlParamsView, this.requestView, 
    			this.responseView, this.validationView};
    	final String basePropName = ObjView.PROP_PRE + name;
    	final Tab[] aTabs = new Tab[components.length];
    	String txt;

    	aTabs[INDEX_HEADERS] = new Tab(ViewField.getTranslation("Headers", this, basePropName + ".headers.tab.lable"));
        aTabs[INDEX_HEADERS].setEnabled(this.headersView.exists());

        aTabs[INDEX_URL_PARAMS] = new Tab(ViewField.getTranslation("Parmeters", this, basePropName + ".urlparams.tab.lable"));
        aTabs[INDEX_URL_PARAMS].setEnabled(this.urlParamsView.exists());
    	
    	aTabs[INDEX_REQUEST] = new Tab(ViewField.getTranslation("Request", this, basePropName + ".request.tab.lable"));
        txt = ViewField.getTranslation("Request", this, basePropName + ".request.tab.title");
        if (!ObjectUtils.isEmpty(txt)) {
        	this.requestView.setText(txt);
        }
        add(createRouteLayout(env, name));

        aTabs[INDEX_RESPONSE] = new Tab(ViewField.getTranslation("Response", this, basePropName + ".response.tab.lable"));
        aTabs[INDEX_RESPONSE].setEnabled(false);
        
        aTabs[INDEX_ERRORS] = new Tab(ViewField.getTranslation("Errors", this, basePropName + ".errors.tab.lable"));
        aTabs[INDEX_ERRORS].setEnabled(false);

        this.tabs = new Tabs(aTabs);
        this.tabs.getStyle().set(EntryPointWrapper.WIDTH, "100%");

        final Div pages = new Div(components);
        final List<Component> pagesShown = Arrays.asList(components);

        pages.getStyle().set(EntryPointWrapper.WIDTH, "100%");
        pages.getStyle().set(EntryPointWrapper.HEIGHT, "100%");
        this.tabs.addSelectedChangeListener(event -> {
        	final int currentindex = this.tabs.getSelectedIndex();
            final boolean enabled = (currentindex == INDEX_REQUEST);

        	this.entryPointSupport.setEnabled(enabled);
    		valid(null, false);
        	for (int index = 0; index < pagesShown.size(); ++index) {
        		pagesShown.get(index).setVisible(index == currentindex);
        	}
        	updateMenu();
        });

        // Added this in here to make the `tabs` actually show on the page
        add(tabs);
        add(pages);
    }

	private Component createRouteLayout(final Environment env, final String viewId) {
        // Route
        build(env, Route.class, viewId);
        // Path
        build(env, Path.class, viewId);

        final HorizontalLayout layout = new HorizontalLayout();

        layout.getStyle().set(EntryPointWrapper.WIDTH, "100%");
        layout.add(this.entryPointSupport.getMethodView());
        for (final Select<?> select : this.entryPointSupport.selects()) {
        	layout.add(select);
        }

        this.buttons = new Button[3];
        this.menu = new VerticalLayout();
//        this.menu.setMargin(false);
//        this.menu.setPadding(false);
//        this.menu.setSpacing(false);
        this.menu.getElement().setAttribute("theme", "menu-vertical"); // set menubar to be vertical with css
        this.menu.setWidth(WIDTH);
        this.menu.setHeight("100%");
        this.buttons[0] 
        		= createIconItem(this.menu, VaadinIcon.CHEVRON_RIGHT, DEFAULT_LBL_SUBMIT, MENU_SUBMIT);
        this.buttons[1] 
        		= createIconItem(this.menu, VaadinIcon.ERASER, DEFAULT_LBL_CLEAN, MENU_CLEAN);
        this.buttons[2] 
        		= createIconItem(this.menu, VaadinIcon.UPLOAD, DEFAULT_LBL_UPLOAD, MENU_UPLOAD);
        layout.add(this.menu);

        return layout;
    }

    private Button createIconItem(final VerticalLayout menu, final VaadinIcon iconName, 
			final String defaultLabel, final String itemName) {
//    	final String label = ViewField.getTranslation(defaultLabel, this, itemName);
	    final Button item = new Button(new Icon(iconName));

//	    item.getElement().setAttribute("aria-label", label);
	    item.addThemeVariants(ButtonVariant.LUMO_ICON, ButtonVariant.LUMO_SMALL);
	    item.addClickListener(this);
	    item.setWidth(ITEM_WIDTH);
	    item.setId(itemName);
	    menu.add(item);

	    return item;
	}
    
    private void updateMenu() {
    	final int currentindex = this.tabs.getSelectedIndex();
    	final boolean[] enabled = menuItemsEnabled[currentindex];

    	for (int index = 0; index < baseMenuItemIds.length; ++index) {
    		this.buttons[index].getElement()
    				.setAttribute("disabled", !enabled[index]);	
    	}
    }

	@Override
	public void onComponentEvent(final ClickEvent<Button> event) {
		final Button item = event.getSource();
		final String id = item.getId().get();

		if (item.isEnabled()) {
	    	final int currentIndex = this.tabs.getSelectedIndex();

	    	if (MENU_SUBMIT.equals(id)) {
				if (currentIndex == INDEX_REQUEST) {
					this.requestView.submit();
				} else {
					logger.warn("Invalid action for menu item \"{}\" in page index {}", 
							id, currentIndex);
				}
			} else if (MENU_CLEAN.equals(id)) {
				if (currentIndex == INDEX_REQUEST) {
					this.requestView.clear();
				} else if (currentIndex == INDEX_RESPONSE) {
					this.responseView.clear();
				} else if (currentIndex == INDEX_ERRORS) {
					this.validationView.clear();
				} else {
					logger.warn("Invalid action for menu item \"{}\" in page index {}", 
							id, currentIndex);
				}
			} else if (MENU_UPLOAD.equals(id)) {
				if (currentIndex == INDEX_REQUEST) {
					this.requestView.upload();
				} else if (currentIndex == INDEX_RESPONSE) {
					this.responseView.upload();
				} else {
					logger.warn("Invalid action for menu item \"{}\" in page index {}", 
							id, currentIndex);
				}
			} else {
				logger.warn("Missing action for menu item \"{}\"", id);
			}
		} else {
			logger.warn("Disable menu item \"{}\"; ignoring click", id);
		}
	}

    @SuppressWarnings("unchecked")
	private <T extends Value<T>> Select<T> build(final Environment env, final Class<T> clazz, 
    		final String viewId) {
        final Select<T> select = new Select<>();

        try {
        	select.addValueChangeListener(this.requestView);
        	this.entryPointSupport.add(select);
			this.entryPointSupport.add(new Values<>(env, clazz, select, viewId));

			final BindingBuilder<EntryPoint, T> builder 
					= this.entryPointSupport.getBinder().forField(select);

			builder.withValidator(new AbstractValidator<T>(ViewLongField.ERROR_MSG) {

				@Override
				public ValidationResult apply(final T value, final ValueContext context) {
	
					if (value == null) {
						return ValidationResult.error(
							MessageFormat.format(
								ViewField.getTranslation("Missing required field {0}", select, 
									"error.missing.field"), 
								clazz.getSimpleName()));
					}
							
					return ValidationResult.ok();
				}

			});
			builder.bind(clazz.getSimpleName().toLowerCase());
		} catch (final InstantiationException | IllegalAccessException | IllegalArgumentException 
				| InvocationTargetException | NoSuchMethodException | SecurityException e) {
			throw new ViewException(e, "Failed to build {1} view for {0}", viewId, 
					clazz.getSimpleName());
		}

        return select;
    }

} // end class RestView
