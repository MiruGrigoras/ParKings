package com.example.huntknow.models


data class Question (
    val question: String ="",
    val qr_group: String ="",
    val variant_a: String ="",
    val variant_b: String ="",
    val variant_c: String ="",
    val variant_d: String ="",
    val correct: String ="" )
//) {
//
//
//    @Exclude
//    fun toMap(): Map<String, Any?> {
//        return mapOf(
//            "question" to question,
//            "qr_group" to qr_group,
//            "correct" to correct,
//            "variant_a" to variant_a,
//            "variant_b" to variant_b,
//            "variant_c" to variant_c,
//            "variant_d" to variant_d
//        )
//    }
//}