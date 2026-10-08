package com.edgechild.ai

import org.junit.Assert.assertEquals
import org.junit.Test

class LanguageDetectorTest {

    @Test
    fun testLanguageDetectionForVariousScripts() {
        val english = LanguageDetector.detect("Why do birds fly?")
        assertEquals("en", english.languageCode)
        assertEquals("en-US", english.localeTag)

        val hindi = LanguageDetector.detect("पक्षी क्यों उड़ते हैं?")
        assertEquals("hi", hindi.languageCode)
        assertEquals("hi-IN", hindi.localeTag)

        val kannada = LanguageDetector.detect("ಪಕ್ಷಿಗಳು ಏಕೆ ಹಾರುತ್ತವೆ?")
        assertEquals("kn", kannada.languageCode)
        assertEquals("kn-IN", kannada.localeTag)

        val telugu = LanguageDetector.detect("పక్షులు ఎందుకు ఎగురుతాయి?")
        assertEquals("te", telugu.languageCode)
        assertEquals("te-IN", telugu.localeTag)

        val tamil = LanguageDetector.detect("பறவைகள் ஏன் பறக்கின்றன?")
        assertEquals("ta", tamil.languageCode)
        assertEquals("ta-IN", tamil.localeTag)

        val arabic = LanguageDetector.detect("لماذا تطير الطيور؟")
        assertEquals("ar", arabic.languageCode)
        assertEquals("ar-SA", arabic.localeTag)

        val chinese = LanguageDetector.detect("鸟儿为什么会飞？")
        assertEquals("zh", chinese.languageCode)
        assertEquals("zh-CN", chinese.localeTag)

        val russian = LanguageDetector.detect("Почему птицы летают?")
        assertEquals("ru", russian.languageCode)
        assertEquals("ru-RU", russian.localeTag)
    }
}
