package com.example.playlistmaker

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.ArrayList


class SearchActivity : AppCompatActivity() {
    private lateinit var inputEditText: EditText
    private lateinit var adapter: TrackAdapter
    private var searchTextValue: String = SEARCH_TEXT_VALUE

    companion object {
        const val SEARCH_TEXT_VALUE = ""
        const val SEARCH_TEXT_KEY = "SEARCH TEXT"
    }

    private val iTunesBaseUrl = "https://itunes.apple.com"
    private val retrofit = Retrofit.Builder()
        .baseUrl(iTunesBaseUrl)
        .addConverterFactory(GsonConverterFactory.create())
        .build()
    private val iTunesService = retrofit.create(ITunesApi::class.java)
    private val trackList = ArrayList<Track>()
    private lateinit var errorPage: LinearLayout
    private lateinit var errorImage: ImageView
    private lateinit var errorText: TextView
    private lateinit var errorButton: Button


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
        val recycler = findViewById<RecyclerView>(R.id.track_list)
        errorPage = findViewById(R.id.placeholder_error)
        errorImage = findViewById(R.id.error_image)
        errorText = findViewById(R.id.error_tv)
        errorButton = findViewById(R.id.error_button)

        recycler.layoutManager = LinearLayoutManager(this)
        adapter = TrackAdapter()
        recycler.adapter = adapter

        backButton.setNavigationOnClickListener {
            finish()
        }

        inputEditText.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                search()
                true
            }
            false
        }

        clearButton.setOnClickListener {
            inputEditText.setText("")
            val imm = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(inputEditText.windowToken, 0)
            inputEditText.clearFocus()
            errorPage.isVisible = false
            trackList.clear()
            adapter.updateData(trackList)
        }

        errorButton.setOnClickListener {
            search()
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
                searchTextValue = s.toString()

            }
        }
        inputEditText.addTextChangedListener(simpleTextWatcher)

    }


    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString(SEARCH_TEXT_KEY, searchTextValue)


    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        searchTextValue = savedInstanceState.getString(SEARCH_TEXT_KEY, SEARCH_TEXT_VALUE)
        inputEditText.setText(searchTextValue)


    }

    private fun search() {
        iTunesService.search(inputEditText.text.toString())
            .enqueue(object : Callback<SearchResponse> {
                override fun onResponse(
                    call: Call<SearchResponse>,
                    response: Response<SearchResponse>
                ) {

                    if (response.code() == 200) {
                        if (response.body()?.results.isNullOrEmpty()) {
                            showPlaceholder(1)

                        } else {
                            showPlaceholder(0)
                            response.body()?.results.let { adapter.updateData(it!!) }
                        }
                    } else {
                        showPlaceholder(2)
                    }
                }

                override fun onFailure(call: Call<SearchResponse>, t: Throwable) {
                    showPlaceholder(2)
                }
            })
    }

    private fun showPlaceholder(code: Int) {
        when (code) {
            0 -> errorPage.isVisible = false
            1 -> {
                trackList.clear()
                adapter.updateData(trackList)
                errorPage.isVisible = true
                errorImage.setImageResource(R.drawable.no_result)
                errorText.text = getString(R.string.error_result_search)
                errorButton.isVisible = false
            }

            2 -> {
                errorPage.isVisible = true
                errorImage.setImageResource(R.drawable.bad_connection)
                errorText.text = getString(R.string.error_bad_connection)
                errorButton.isVisible = true
            }

        }
    }
}