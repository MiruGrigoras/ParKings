package com.example.huntknow.ui.main

import android.content.Context
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import com.example.huntknow.R
import com.example.huntknow.models.Question

private val TAB_TITLES = arrayOf(
    R.string.question_text_1,
    R.string.question_text_2,
    R.string.question_text_3,
    R.string.question_text_4,
    R.string.question_text_5,
    R.string.question_text_6,
    R.string.question_text_7,
    R.string.question_text_8,
    R.string.question_text_9,
    R.string.question_text_10

)

/**
 * A [FragmentPagerAdapter] that returns a fragment corresponding to
 * one of the sections/tabs/pages.
 */
class SectionsPagerAdapter(private val context: Context, fm: FragmentManager, private val quiz : MutableList<Question>) :
    FragmentPagerAdapter(fm) {


    override fun getItem(position: Int): Fragment {
        // getItem is called to instantiate the fragment for the given page.
        // Return a QuizFragment (defined as a static inner class below).
        return QuizFragment.newInstance(
            position + 1,
            quiz.getOrNull(position)!!
        )
    }

    override fun getPageTitle(position: Int): CharSequence? {
        return context.resources.getString(TAB_TITLES[position])
    }

    override fun getCount(): Int {
        return TAB_TITLES.size
    }

//    fun getCurrentPosition(): Int {
//        return getItemPosition()
//    }

}