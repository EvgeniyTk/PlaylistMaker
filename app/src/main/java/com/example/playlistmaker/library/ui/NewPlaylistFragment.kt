package com.example.playlistmaker.library.ui

import android.content.res.ColorStateList
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.playlistmaker.R
import com.example.playlistmaker.databinding.FragmentNewPlaylistBinding
import com.example.playlistmaker.library.view_model.NewPlaylistViewModel
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import org.koin.androidx.viewmodel.ext.android.viewModel

open class NewPlaylistFragment : Fragment() {

    open val viewModel by viewModel<NewPlaylistViewModel>()
    private var _binding: FragmentNewPlaylistBinding? = null
    protected val binding get() = _binding!!

    private lateinit var textInputLayoutName: TextInputLayout
    private lateinit var textInputEditTextName: TextInputEditText
    private lateinit var textInputLayoutDescription: TextInputLayout
    private lateinit var textInputEditTextDescription: TextInputEditText
    private lateinit var confirmDialog: MaterialAlertDialogBuilder


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentNewPlaylistBinding.inflate(layoutInflater, container, false)
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        ViewCompat.setOnApplyWindowInsetsListener(binding.newPlaylist) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, 0)
            insets
        }

        ViewCompat.setOnApplyWindowInsetsListener(binding.scroll) { v, insets ->
            val imeVisible = insets.isVisible(WindowInsetsCompat.Type.ime())
            val imeHeight = insets.getInsets(WindowInsetsCompat.Type.ime()).bottom
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())

            v.setPadding(
                systemBars.left,
                0,
                systemBars.right,
                if (imeVisible) imeHeight else systemBars.bottom
            )

            if (imeVisible) {
                binding.scroll.post {
                    binding.scroll.smoothScrollTo(0, binding.newPlaylistDescriptionTil.bottom)
                }
            }

            insets
        }

        viewModel.selectedUri.observe(viewLifecycleOwner) {
            if (it != null) {
                binding.newPlaylistIv.setImageURI(it)
            }
        }

        viewModel.saveCompleted.observe(viewLifecycleOwner) {
            if (this !is EditPlaylistFragment) {
                Toast.makeText(
                    requireContext(),
                    getString(R.string.playlist_created, it), Toast.LENGTH_SHORT
                )
                    .show()
            }
            findNavController().navigateUp()
        }


        confirmDialog = MaterialAlertDialogBuilder(requireContext(), R.style.CustomAlertDialogTheme)
            .setTitle(getString(R.string.confirm_playlist_creation_title))
            .setMessage(getString(R.string.confirm_playlist_creation_message))
            .setNeutralButton(getString(R.string.cansel)) { dialog, which -> }
            .setPositiveButton(getString(R.string.finish)) { dialog, which ->
                findNavController().navigateUp()
            }

        textInputLayoutName = binding.newPlaylistNameTil
        textInputEditTextName = binding.newPlaylistNameEt
        textInputLayoutDescription = binding.newPlaylistDescriptionTil
        textInputEditTextDescription = binding.newPlaylistDescriptionEt

        binding.newPlaylistTb.setNavigationOnClickListener {
            onBackPressed()
        }

        requireActivity().onBackPressedDispatcher.addCallback(
            viewLifecycleOwner,
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    onBackPressed()
                }
            }
        )

        textInputEditTextName.setOnFocusChangeListener { _, _ ->
            updateColors(textInputLayoutName, textInputEditTextName)
        }

        textInputEditTextName.doAfterTextChanged {
            updateColors(textInputLayoutName, textInputEditTextName)
            binding.newPlaylistBtn.isEnabled = !binding.newPlaylistNameEt.text.isNullOrBlank()
        }

        textInputEditTextDescription.setOnFocusChangeListener { _, _ ->
            updateColors(textInputLayoutDescription, textInputEditTextDescription)
        }

        textInputEditTextDescription.doAfterTextChanged {
            updateColors(textInputLayoutDescription, textInputEditTextDescription)
        }

        val pickMedia =
            registerForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
                if (uri != null) {
                    binding.newPlaylistIv.setImageURI(uri)
                    viewModel.setUri(uri)
                } else {
                    Log.d("PhotoPicker", "No media selected")
                }
            }

        binding.newPlaylistBtn.setOnClickListener {
            val albumName = binding.newPlaylistNameEt.text.toString()
            val albumDescription = binding.newPlaylistDescriptionEt.text.toString()
            viewModel.savePlaylist(albumName, albumDescription)
        }

        binding.newPlaylistIv.setOnClickListener {
            pickMedia.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
        }

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }


    private fun updateColors(
        textInputLayout: TextInputLayout,
        textInputEditText: TextInputEditText
    ) {
        val isNotEmpty = textInputEditText.text?.isNotEmpty() == true

        val blue = ContextCompat.getColor(requireContext(), R.color.blue)
        val grey = ContextCompat.getColor(requireContext(), R.color.grey)

        val states = arrayOf(
            intArrayOf(android.R.attr.state_focused),
            intArrayOf()
        )
        val colors = intArrayOf(
            blue,
            if (isNotEmpty) blue else grey
        )
        val colorStateList = ColorStateList(states, colors)

        textInputLayout.setBoxStrokeColorStateList(colorStateList)
        textInputLayout.defaultHintTextColor = colorStateList
    }

    private fun onBackPressed() {
        val hasName = !textInputEditTextName.text.isNullOrEmpty()
        val hasDescription = !textInputEditTextDescription.text.isNullOrEmpty()
        val hasImage = viewModel.selectedUri.value != null

        if (hasName || hasDescription || hasImage) {
            confirmDialog.show()
        } else {
            findNavController().navigateUp()
        }
    }
}