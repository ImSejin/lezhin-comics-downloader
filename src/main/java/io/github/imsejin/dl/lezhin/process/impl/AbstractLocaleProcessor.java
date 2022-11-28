package io.github.imsejin.dl.lezhin.process.impl;

import io.github.imsejin.dl.lezhin.exception.LezhinComicsDownloaderException;
import io.github.imsejin.dl.lezhin.process.ProcessContext;
import io.github.imsejin.dl.lezhin.process.Processor;

import java.util.Locale;

public abstract class AbstractLocaleProcessor implements Processor {

    @Override
    public final Object process(ProcessContext context) throws LezhinComicsDownloaderException {
        // Resolves an implementation for the locale.
        Locale locale = context.getLanguage().getValue();

        if (Locale.KOREA.equals(locale)) {
            return processForKorean(context);
        } else if (Locale.US.equals(locale)) {
            return processForEnglish(context);
        } else if (Locale.JAPAN.equals(locale)) {
            return processForJapanese(context);
        } else {
            throw new IllegalArgumentException("ProcessContext.language.value is not recognized: " + locale);
        }
    }

    // -------------------------------------------------------------------------------------------------

    protected abstract Object processForKorean(ProcessContext context) throws LezhinComicsDownloaderException;

    protected abstract Object processForEnglish(ProcessContext context) throws LezhinComicsDownloaderException;

    protected abstract Object processForJapanese(ProcessContext context) throws LezhinComicsDownloaderException;

}
