package com.example.playlistmaker

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.ImageView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.isVisible


class SearchActivity : AppCompatActivity() {
    private lateinit var inputEditText: EditText
    private var text: String = TEXT_VALUE

    companion object {
        const val EDIT_TEXT_KEY = "KEY"
        const val TEXT_VALUE = ""
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_search)
        inputEditText = findViewById(R.id.searchInputEditText)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.search)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }


        val clearButton = findViewById<ImageView>(R.id.clear_button)
        val backButton = findViewById<Toolbar>(R.id.toolbar_search)

        backButton.setNavigationOnClickListener {
            finish()
        }

        clearButton.setOnClickListener {
            inputEditText.setText("")
            val imm = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(inputEditText.windowToken, 0)
            inputEditText.clearFocus()
        }

        val simpleTextWatcher = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                // empty
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

                clearButton.isVisible = !s.isNullOrEmpty()

            }

            override fun afterTextChanged(s: Editable?) {
                // empty
                text = s.toString()

            }
        }
        inputEditText.addTextChangedListener(simpleTextWatcher)

    }


    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        Log.d("TextSave", "Текущий текст: $text") // проверка сохранения текста
        outState.putString(EDIT_TEXT_KEY, text)
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        text = savedInstanceState.getString(EDIT_TEXT_KEY, TEXT_VALUE)
        Log.d("TextSave", "Новый текст: $text") // проверка восстановления текста
        inputEditText.setText(text)
    }

}