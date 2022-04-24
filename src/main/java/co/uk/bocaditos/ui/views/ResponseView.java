package co.uk.bocaditos.ui.views;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.core.exc.StreamReadException;
import com.fasterxml.jackson.databind.DatabindException;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.component.upload.Upload;
import com.vaadin.flow.component.upload.UploadI18N;
import com.vaadin.flow.component.upload.receivers.MemoryBuffer;

import co.uk.bocaditos.ui.data.entity.JsonComparator;
import co.uk.bocaditos.ui.data.entity.JsonComparator.ErrorMsg;


/**
 * Response view.
 * 
 * @param <P> the response bean type.
 * 
 * @author aasco
 */
@SuppressWarnings("serial")
class ResponseView<P> extends VerticalLayout {

	private static final Logger logger = LoggerFactory.getLogger(RestView.class);

    private final TextArea textArea;
    private final Upload upload;

    private final ValidationErrorListener listener;
    private final TabsWithMain main;


    /**
     * I18n support for upload button.
     */
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


    public ResponseView(final String path, final ValidationErrorListener listener, 
    		final TabsWithMain main) {
    	this.listener = listener;
    	this.main = main;
        getStyle().set(EntryPointWrapper.WIDTH, "100%");
        getStyle().set(EntryPointWrapper.HEIGHT, "100%");
        this.textArea = new TextArea();
        this.textArea.setWidthFull();
        this.textArea.setHeightFull();
        this.textArea.setReadOnly(true);
        add(this.textArea);
        this.upload = upload(path);
        add(this.upload);
    	setVisible(false);
    }
    
    public void update(final Class<?> clazz) {
    	// Nothing to do for now
    }
	
	public void clear() {
		setValue("");
		clearUpload();
	}
    
    void clearUpload() {
    	this.upload.getElement().executeJs("this.files=[]");
    }

    void upload() {
    	if (!((Button) this.upload.getUploadButton()).isEnabled()) {
    		return;
    	}
    	((Button) this.upload.getUploadButton()).click();
    }

	public void setValue(final String response) {
		this.textArea.setValue(response);
		setVisible(response != null && !response.isEmpty());
		if (!isVisible()) {
			this.main.setMainTab(RestView.INDEX_RESPONSE);
		}
	}

    protected void processFile(final InputStream fileData, final String fileName, 
    		final long contentLength, final String mimeType) throws StreamReadException, 
    		DatabindException, IOException {
    	// Verify
    	final List<ErrorMsg> errors = JsonComparator.compare(
    			new BufferedReader(new InputStreamReader(fileData, StandardCharsets.UTF_8))
    	        .lines()
    	        .collect(Collectors.joining("\n")), this.textArea.getValue());

    	if (!errors.isEmpty()) {
    		this.listener.processErrors(errors);
    	}
    }

    private Upload upload(final String basePath) {
    	final StringBuilder path = new StringBuilder(basePath);
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
	
} // end class ResponseView
