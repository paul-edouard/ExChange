package com.coherentlogic.fred.client.core.converters;

import java.io.IOException;
import java.io.InputStream;

import org.springframework.http.HttpInputMessage;
import org.springframework.http.HttpOutputMessage;
import org.springframework.http.MediaType;
import org.springframework.http.converter.AbstractHttpMessageConverter;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.http.converter.HttpMessageNotWritableException;

import com.coherentlogic.fred.client.core.domain.Message;
import com.coherentlogic.fred.client.core.domain.Observations;

/**
 * A message converter that supports the application/zip media type. This is
 * used when calls are made to the fred/series/observations web service and the
 * file_type is set to xsl or txt -- this will return a compressed txt/xsl file.
 *
 * @author <a href="support@coherentlogic.com">Support</a>
 */
public class ApplicationZipMessageConverter
    extends AbstractHttpMessageConverter<Observations> {

    public static final String APPLICATION = "application", ZIP = "zip",
        TXT = "txt";

    public ApplicationZipMessageConverter() {
        super(new MediaType(APPLICATION, ZIP));
    }

    @Override
    protected boolean supports(Class<?> clazz) {
        return Observations.class.equals(clazz);
    }

    @Override
    protected Observations readInternal(Class<? extends Observations> clazz,
        HttpInputMessage inputMessage) throws IOException,
        HttpMessageNotReadableException {

        Observations result = new Observations ();

        InputStream in = inputMessage.getBody();

        Message content = new Message (in);

        result.setMessage(content);

        return result;
    }

    @Override
    protected void writeInternal(Observations t, HttpOutputMessage outputMessage)
            throws IOException, HttpMessageNotWritableException {
        throw new UnsupportedOperationException(
            "The writeInternal method has not been implemented.");
    }
}
