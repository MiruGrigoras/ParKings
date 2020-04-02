package com.example.huntknow.ui.main

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel

class QuestionView : ViewModel() {


    private val _question = MutableLiveData<String>()
    private val _variantA = MutableLiveData<String>()
    private val _variantB = MutableLiveData<String>()
    private val _variantC = MutableLiveData<String>()
    private val _variantD = MutableLiveData<String>()
    private val _answer = MutableLiveData<String>()

    val questionText: LiveData<String> = Transformations.map(_question) {
        it
    }
    val variantAText: LiveData<String> = Transformations.map(_variantA) {
        it
    }
    val variantBText: LiveData<String> = Transformations.map(_variantB) {
        it
    }
    val variantCText: LiveData<String> = Transformations.map(_variantC) {
        it
    }
    val variantDText: LiveData<String> = Transformations.map(_variantD) {
        it
    }
    val answerText: LiveData<String> = Transformations.map(_answer) {
        it
    }


    fun setQuestion(question: String)
    {
        _question.value = question
    }
    fun setVariantA(variantA: String)
    {
        _variantA.value = variantA
    }
    fun setVariantB(variantB: String)
    {
        _variantB.value = variantB
    }
    fun setVariantC(variantC: String)
    {
        _variantC.value = variantC
    }
    fun setVariantD(variantD: String)
    {
        _variantD.value = variantD
    }
    fun setAnswer(answer: String)
    {
        _answer.value = answer
    }


}