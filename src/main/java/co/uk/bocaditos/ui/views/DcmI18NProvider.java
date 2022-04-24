package co.uk.bocaditos.ui.views;

import java.text.MessageFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import static java.util.Arrays.asList;
import static java.util.Collections.unmodifiableList;
import static java.util.ResourceBundle.getBundle;
import static java.util.Locale.ENGLISH;
import java.util.Map;
import java.util.ResourceBundle;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.vaadin.flow.i18n.I18NProvider;


public class DcmI18NProvider implements I18NProvider {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1600139534192605171L;
	private static final Logger logger = LoggerFactory.getLogger(DcmI18NProvider.class);

	public static final String RESOURCE_BUNDLE_NAME = "messages";
	public static final String NULL_SUFIX = " - null";

	private static final Locale defaultLocale = ENGLISH;
	private static final Locale ESP = new Locale("es", "ES");
	private static final List<Locale> providedLocales = unmodifiableList(asList(ENGLISH, ESP));
	private static final Map<Locale, ResourceBundle> bundles;
	

	static {
		bundles = new HashMap<>();
		bundles.put(ENGLISH, getBundle(RESOURCE_BUNDLE_NAME, ENGLISH));
		bundles.put(ESP, getBundle(RESOURCE_BUNDLE_NAME, ESP));
	}


	public DcmI18NProvider() {
		logger.info("{} was found", DcmI18NProvider.class.getSimpleName());
	}

	@Override
	public List<Locale> getProvidedLocales() {
	    return providedLocales;
	}

	@Override
	public String getTranslation(final String key, final Locale locale, final Object... params) {
		ResourceBundle rsrcBundle = bundles.get(locale);

		if (rsrcBundle == null) {
			rsrcBundle = bundles.get(defaultLocale);
	    }

	    if (!rsrcBundle.containsKey(key)) {
	         logger.debug("Missing resource key (i18n) {}", key);

	         return key + NULL_SUFIX;
	    }

	    return MessageFormat.format(rsrcBundle.getString(key), params);
	}

	
} // end class DcmI18NProvider
