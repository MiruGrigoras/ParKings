package com.example.huntknow.ui.main

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.CheckBox
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.example.huntknow.R
import com.example.huntknow.models.Common
import com.example.huntknow.models.Question
import com.example.huntknow.GlobalVariables.Companion.right_answers

/**
 * A placeholder fragment containing a simple view.
 */
class QuizFragment : Fragment() {

    private lateinit var questionView: QuestionView
    private lateinit var question: TextView
    private lateinit var variantA: CheckBox
    private lateinit var variantB: CheckBox
    private lateinit var variantC: CheckBox
    private lateinit var variantD: CheckBox
    private lateinit var submitAnswer: Button
    private lateinit var correct: String
    var a = Common()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        questionView = ViewModelProviders.of(this).get(QuestionView::class.java).apply {
            setQuestion(arguments?.getString(ARG_QUESTION) ?: "")
            setVariantA(arguments?.getString(ARG_VARIANT_A) ?: "")
            setVariantB(arguments?.getString(ARG_VARIANT_B) ?: "")
            setVariantC(arguments?.getString(ARG_VARIANT_C) ?: "")
            setVariantD(arguments?.getString(ARG_VARIANT_D) ?: "")
            setAnswer(arguments?.getString(ARG_ANSWER) ?: "")
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.fragment_quiz, container, false)
        question = root.findViewById(R.id.question)
        variantA = root.findViewById(R.id.variant_a)
        variantB = root.findViewById(R.id.variant_b)
        variantC = root.findViewById(R.id.variant_c)
        variantD = root.findViewById(R.id.variant_d)
        submitAnswer = root.findViewById(R.id.submit)

        questionView.questionText.observe(this, Observer<String> {
            question.text = it
        })

        questionView.variantAText.observe(this, Observer<String> {
            variantA.text = it
        })
        variantA.setOnCheckedChangeListener { _, isChecked ->

            if(isChecked)
                a.answers.add("A")
            else
                a.answers.remove("A")
        }

        questionView.variantBText.observe(this, Observer<String> {
            variantB.text = it
        })
        variantB.setOnCheckedChangeListener { _, isChecked ->

            if(isChecked)
                a.answers.add("B")
            else
                a.answers.remove("B")
        }

        questionView.variantCText.observe(this, Observer<String> {
            variantC.text = it
        })
        variantC.setOnCheckedChangeListener { _, isChecked ->

            if(isChecked)
                a.answers.add("C")
            else
                a.answers.remove("C")
        }

        questionView.variantDText.observe(this, Observer<String> {
            variantD.text = it
        })
        variantD.setOnCheckedChangeListener { _, isChecked ->

            if(isChecked)
                a.answers.add("D")
            else
                a.answers.remove("D")
        }

        questionView.answerText.observe(this, Observer<String> {
            correct = it
        })


        submitAnswer.setOnClickListener{
            submitAnswer.visibility = View.INVISIBLE
            if(a.answers.size == 1) {
                if (a.answers.first() == correct)
                    right_answers ++

            }
            showCorrectAnswer()
            disableAnswer()

        }

        return root
    }
    fun showCorrectAnswer(){

        when(correct){
            "A" -> variantA.setTextColor(Color.RED)
            "B" -> variantB.setTextColor(Color.RED)
            "C" -> variantC.setTextColor(Color.RED)
            "D" -> variantD.setTextColor(Color.RED)
        }
    }

    fun disableAnswer(){

        variantA.isEnabled = false
        variantB.setEnabled(false)
        variantC.setEnabled(false)
        variantD.setEnabled(false)
    }

    companion object {
        /**
         * The fragment argument representing the section number for this
         * fragment.
         */
        private const val ARG_SECTION_NUMBER = "section_number"
        private const val ARG_QUESTION = "question"
        private const val ARG_VARIANT_A = "variant_a"
        private const val ARG_VARIANT_B = "variant_b"
        private const val ARG_VARIANT_C = "variant_c"
        private const val ARG_VARIANT_D = "variant_d"
        private const val ARG_ANSWER = "correct"

        /**
         * Returns a new instance of this fragment for the given section
         * number.
         */
        @JvmStatic
        fun newInstance(sectionNumber: Int, question : Question): QuizFragment {
            return QuizFragment().apply {
                arguments = Bundle().apply {
                    putInt(ARG_SECTION_NUMBER, sectionNumber)
                    putString(ARG_QUESTION,question.question)
                    putString(ARG_VARIANT_A,question.variant_a)
                    putString(ARG_VARIANT_B,question.variant_b)
                    putString(ARG_VARIANT_C,question.variant_c)
                    putString(ARG_VARIANT_D,question.variant_d)
                    putString(ARG_ANSWER,question.correct)
                }
            }
        }
    }
}