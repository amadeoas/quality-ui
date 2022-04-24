package co.uk.bocaditos.ui.views;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.net.URISyntaxException;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.ObjectUtils;

import com.fasterxml.jackson.core.exc.StreamReadException;
import com.fasterxml.jackson.databind.DatabindException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.HasValue.ValueChangeEvent;
import com.vaadin.flow.component.HasValue.ValueChangeListener;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.upload.Upload;
import com.vaadin.flow.component.upload.UploadI18N;
import com.vaadin.flow.component.upload.receivers.MemoryBuffer;
import com.vaadin.flow.data.binder.Binder;

import co.uk.bocaditos.ui.data.service.ObjService;
import co.uk.bocaditos.ui.data.service.WrapperRestData;


/**
 * Support to fill a form from an object.
 * 
 * @author aasco
 *
 * @param <Q> the request bean type.
 * @param <P> the response bean type.
 */
@SuppressWarnings("rawtypes")
public class ObjView<Q, P> extends Div implements ValueChangeListener {

    /**
	 * 
	 */
	private static final long serialVersionUID = 9176810147113939103L;

	private static final Logger logger = LoggerFactory.getLogger(ObjView.class);
	
	private static final String NAME = "ObjView";

	static final String PROP_PRE  = "view.";
	static final String PROP_CLEAR  = PROP_PRE + "button.clear";
	private static final String PROP_SUBMIT = PROP_PRE + "button.submit";
	private static final String PROP_SUBMIT_NOTIFICATION_POST = ".request.submit.notification";
	private static final String PROP_TITLE_POST = ".request.title";

	private final Button clear;
    private final Upload upload;
    private final Button submit;

    private final RestInfo<Q> restInfo;
    private final Binder<Q> binder;
    private final transient Constructor<Q> constructor;
    private final Class<Q> clazz;
    private final ObjForm<Q> form;
	private Boolean valid;
	private final ValidChangeListener changeListener;
    

    /**
     * I18n support for upload button.
     */
    @SuppressWarnings("serial")
	class ObjUploadI18N extends UploadI18N {

    	public ObjUploadI18N(final StringBuilder path, final Upload upload) {
        	final int length = path.length();

        	path.append("upload.");
        	setDropFiles(new DropFiles()
                    .setOne(getLabel(path, "dropLabel", "Drop JSON file here")));
        	setAddFiles(new AddFiles()
                    .setOne(getLabel(path, "addLabel", "Upload JSON file...")));
        	path.append("error.");
        	setError(new Error()
        			.setTooManyFiles(getLabel(path, "tooMany", "Too Many Files"))
                    .setFileIsTooBig(getLabel(path, "tooBig", "File is Too Big"))
                    .setIncorrectFileType(getLabel(path, "incorrect", "The provided file does not have the correct format. Please, provide a JSON document")));
        	path.setLength(length);
        	upload.setI18n(this);

        	upload.setMaxFiles(1);
    	}
    	
    	private String getLabel(final StringBuilder path, final String labelName, 
    			final String labelDefault) {
    		final int length = path.length();
    		String label;

    		path.append(labelName);
    		label = getTranslation(path.toString());
    		if (label.endsWith("null")) {
    			label = labelDefault;
    		}
    		path.setLength(length);

    		return label;
    	}
    	
    } // end class ObjUploadI18N


    public ObjView(final String name, final Class<Q> clazz, final ObjService<Q, P> objService, 
    		final ResponseListener<P> listener, final ValidChangeListener changeListener, 
    		final RestInfo<Q> restInfo) throws SecurityException, NoSuchMethodException {
		final StringBuilder path = new StringBuilder(100);

		getStyle().set("padding-left", "5px");
    	getStyle().set("padding-right", "5px");
    	this.clazz = clazz;
    	this.changeListener = changeListener;
    	this.form = createFormLayout(path);
    	this.restInfo = restInfo;
    	this.binder = this.form.binder();
    	this.binder.addStatusChangeListener(e -> validate());
    	addClassName(buildClassName());

        createTitle(name);
        add(this.form);

        this.clear = new Button(getTranslation(PROP_CLEAR));
        this.submit = new Button(getTranslation(PROP_SUBMIT));
        this.submit.setEnabled(false);
        this.submit.addClickListener(e -> {
			try {
	        	final WrapperRestData<Q> ep = new WrapperRestData<Q>(this.restInfo);

	        	listener.change(objService.update(ep));
	        	Notification.show(getTranslation(PROP_PRE + name + PROP_SUBMIT_NOTIFICATION_POST));
			} catch (final URISyntaxException use) {
				// TODO: 
			}
        });
        this.upload = upload(path);
        add(createButtonLayout(path, listener));

        this.constructor = this.clazz.getConstructor();
    }
    
    public boolean isValid() {
    	return this.restInfo.validate().isOk() && this.binder.validate().isOk();
    }
    
    public void update(final Class<?> clazz) {
    	// TODO
    }
    
    public Q getBean() {
    	return this.binder.getBean();
    }

	@Override
	public void valueChanged(final ValueChangeEvent event) {
		validate();
	}
	
	void setSubmitEnabled(final boolean enabled) {
		this.submit.setEnabled(enabled);
	}
	
	Map<String, String> getHeaders() {
		return null;
	}
	
	Map<String, String> getQueryParameters() {
		return null;
	}
    
    void submit() {
    	if (!this.submit.isEnabled()) {
    		return;
    	}
    	this.submit.click();
    }
    
    void upload() {
    	if (this.upload.getUploadButton() == null 
    			|| !((Button) this.upload.getUploadButton()).isEnabled()) {
    		return;
    	}
    	((Button) this.upload.getUploadButton()).click();
    }

    void clear() {
    	try {
    		this.restInfo.setNewBean();
			this.binder.setBean(this.constructor.newInstance()); // TODO: for classes with other classes as class fields
		} catch (final InstantiationException | IllegalAccessException | IllegalArgumentException
				| InvocationTargetException e) {
			logger.warn("Failed to clear form: " + e.getMessage(), e);
		}
		clearUpload();
    }
    
    void clearUpload() {
    	this.upload.getElement().executeJs("this.files=[]");
    }

    private void createTitle(final String name) {
    	final String txt = getTranslation(PROP_PRE + name + PROP_TITLE_POST);

    	if (ObjectUtils.isEmpty(txt)) {
    		add(new H3(txt));
    	}
    }

    private ObjForm<Q> createFormLayout(final StringBuilder path) {
        return new ObjForm<>(this.clazz, path, this);
    }

    private Component createButtonLayout(final StringBuilder path, 
    		final ResponseListener<P> listener) {
        final HorizontalLayout buttonsLayout = new HorizontalLayout();

        buttonsLayout.addClassName("button-layout");
        this.submit.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        buttonsLayout.add(this.submit);
        buttonsLayout.add(this.clear);
        buttonsLayout.add(this.upload);
        this.clear.addClickListener(e -> listener.clearForm());

        return buttonsLayout;
    }
    
    private Upload upload(final StringBuilder path) {
    	final MemoryBuffer buffer = new MemoryBuffer();
    	final Upload upload = new Upload(buffer);
    	
    	upload.setDropAllowed(true);
    	upload.setAcceptedFileTypes("application/json", ".json");
    	new ObjUploadI18N(path, upload);
    	upload.addSucceededListener(event -> {
    	    try {
				processFile(buffer.getInputStream(), event.getFileName(), event.getContentLength(), 
						event.getMIMEType());
			} catch (final IOException ioe) {
				logger.error("Failed to load file: " + ioe.getMessage(), ioe);
			}
    	});

        return upload;
    }

    protected void processFile(final InputStream fileData, final String fileName, 
    		final long contentLength, final String mimeType) throws StreamReadException, 
    		DatabindException, IOException {
    	final ObjectMapper obj = new ObjectMapper();
    	final Q value = obj.readValue(fileData, this.clazz);

    	this.form.update(value);
    }
    
    private String buildClassName() {
    	final String viewClassName = getClass().getSimpleName();
    	final StringBuilder buf = new StringBuilder(viewClassName.length() + 4);
    	int from = 0;
    	int to;

    	for (to = 1; to < viewClassName.length(); ++to) {
    		if (Character.isUpperCase(viewClassName.charAt(to))) {
    			if (buf.length() > 0) {
    				buf.append('-');
    			}
    	    	buf.append(Character.toLowerCase(viewClassName.charAt(from)));
    			buf.append(viewClassName.substring(from + 1, to));
    			from = to;
    		}
    	}
    	if (to == viewClassName.length()) {
	    	buf.append(Character.toLowerCase(viewClassName.charAt(from)));
			buf.append(viewClassName.substring(from + 1, to));
    	}

    	return buf.toString();
    }
    
    static String buildPropName(final Class<?> clazz, final String postName) {
    	return buildBasePropName(clazz) + postName;
    }
    
    static String buildBasePropName(final Class<?> clazz) {
    	return PROP_PRE + clazz.getSimpleName()
    			.substring(0, clazz.getSimpleName().length() - "View".length()).toLowerCase();
    }

	private void validate() {
		final boolean valid = this.binder.isValid();

		if (this.valid == null || this.valid != valid) {
			this.valid = valid;
			this.changeListener.valid(NAME, this.valid);
		}
	}

} // end class ObjView
