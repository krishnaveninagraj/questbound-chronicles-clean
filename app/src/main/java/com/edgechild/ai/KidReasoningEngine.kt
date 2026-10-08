package com.edgechild.ai

object KidReasoningEngine {

    fun getKidFriendlyAnswer(detectedObject: String, question: String, language: String): String {
        val lowerQ = question.lowercase()
        val cleanObj = detectedObject.substringBefore(" (").lowercase()

        return when (language) {
            "Kannada" -> getKannadaAnswer(cleanObj, lowerQ)
            "Hindi" -> getHindiAnswer(cleanObj, lowerQ)
            "Tamil" -> getTamilAnswer(cleanObj, lowerQ)
            else -> getEnglishAnswer(cleanObj, lowerQ)
        }
    }

    private fun getEnglishAnswer(obj: String, question: String): String {
        return when {
            question.contains("road") || question.contains("black") || obj.contains("traffic") -> 
                "Roads are black because they are made of asphalt! The dark color absorbs sunlight heat so rainwater dries up super fast for cars and bikes."
            question.contains("sky") || obj.contains("blue") -> 
                "The sky is blue because sunlight scatters across Earth's air, bouncing blue light all around us!"
            obj.contains("dog") -> "Woof! Dogs are faithful friends who love to run, play catch, and protect their homes!"
            obj.contains("cat") -> "Meow! Cats are curious explorers who love to chase toys and nap in warm sunlight."
            else -> "That is a $obj! Isn't our world filled with amazing things to discover?"
        }
    }

    private fun getKannadaAnswer(obj: String, question: String): String {
        return when {
            question.contains("road") || question.contains("black") -> 
                "Rastegalu kappagiruvanake kaarana avu tarinal (asphalt) inda maadapative. Idu suryana bayalu tegedukondu neervannu bega oorisutte."
            question.contains("sky") -> "Aakasha neeli bannadalliralu kaarana suryana kirana galiyalli vistarisuwudu."
            else -> "Idu ondu $obj! Idu namma jagattannu sundaravagisutte."
        }
    }

    private fun getHindiAnswer(obj: String, question: String): String {
        return when {
            question.contains("road") || question.contains("black") -> 
                "Sarkein kaali isliye hoti hain kyunki ye asphalt se bani hoti hain, jo dhoop ko sokh kar barish ke paani ko jaldi sukha deta hai!"
            question.contains("sky") -> "Aasman neela isliye dikhta hai kyunki suraj ki roshni hawa mein phail kar neela rang bikher deti hai."
            else -> "Yeh ek $obj hai! Hamari duniya kitni sundar hai."
        }
    }

    private fun getTamilAnswer(obj: String, question: String): String {
        return when {
            question.contains("road") || question.contains("black") -> 
                "Salai karuppaka iruppatharku karanam athu asphalt-al seyyappattullathu. Athu veyilai urainthu mazhai neerai sekiram ulanthu vidum!"
            else -> "Ithu oru $obj! Intha ulagam romba alaganalathu."
        }
    }
}
