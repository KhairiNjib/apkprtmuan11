package com.example.apkprtmuan11

import android.os.Bundle
import android.text.InputType
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.ListView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.apkprtmuan11.data.api.RetrofitClient
import com.example.apkprtmuan11.data.model.Post
import com.example.apkprtmuan11.data.model.PostRequest
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.JsonParser
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.launch
import okhttp3.ResponseBody
import retrofit2.HttpException
import java.io.IOException

class MainActivity : AppCompatActivity() {
    private val api by lazy { RetrofitClient.apiService }
    private val gson: Gson = GsonBuilder().setPrettyPrinting().create()
    private lateinit var adapter: ReportAdapter
    private lateinit var progressBar: ProgressBar
    private lateinit var addButton: View
    private lateinit var reloadButton: View
    private lateinit var reportList: ListView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        progressBar = findViewById(R.id.progressBar)
        addButton = findViewById(R.id.addButton)
        reloadButton = findViewById(R.id.reloadButton)
        reportList = findViewById(R.id.reportList)
        adapter = ReportAdapter(this)
        reportList.adapter = adapter
        reportList.emptyView = findViewById(R.id.emptyView)

        addButton.setOnClickListener { showPostForm() }
        reloadButton.setOnClickListener { loadReports() }
        reportList.setOnItemClickListener { _, _, position, _ ->
            loadReportDetail(adapter.getItem(position))
        }
        loadReports()
    }

    private fun loadReports() = runRequest {
        adapter.replace(api.getPosts())
    }

    private fun loadReportDetail(post: Post) = runRequest {
        val response = api.getPost(post.id)
        val detailedPost = response.body()
        if (response.isSuccessful && detailedPost != null) {
            showDetail(detailedPost, response.code())
        } else {
            showResponse(response.code(), response.errorBody())
        }
    }

    private fun showDetail(post: Post, statusCode: Int) {
        val dialog = AlertDialog.Builder(this)
            .setTitle(post.title)
            .setMessage("ID: ${post.id}\n\n${post.body}\n\nHTTP $statusCode")
            .setPositiveButton("Edit (PUT)", null)
            .setNeutralButton("Ubah judul (PATCH)", null)
            .setNegativeButton("Hapus", null)
            .create()

        dialog.setOnShowListener {
            dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener {
                dialog.dismiss()
                showPostForm(post)
            }
            dialog.getButton(AlertDialog.BUTTON_NEUTRAL).setOnClickListener {
                dialog.dismiss()
                showPatchForm(post)
            }
            dialog.getButton(AlertDialog.BUTTON_NEGATIVE).setOnClickListener {
                dialog.dismiss()
                confirmDelete(post)
            }
        }
        dialog.show()
    }

    private fun showPostForm(post: Post? = null) {
        val titleInput = EditText(this).apply {
            hint = "Nama / kode mesin"
            inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_FLAG_CAP_SENTENCES
            setText(post?.title.orEmpty())
            setSingleLine(true)
        }
        val bodyInput = EditText(this).apply {
            hint = "Deskripsi kerusakan, lokasi, dan prioritas"
            inputType = InputType.TYPE_CLASS_TEXT or
                InputType.TYPE_TEXT_FLAG_CAP_SENTENCES or
                InputType.TYPE_TEXT_FLAG_MULTI_LINE
            minLines = 3
            gravity = android.view.Gravity.TOP
            setText(post?.body.orEmpty())
        }
        val form = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            val horizontalPadding = (24 * resources.displayMetrics.density).toInt()
            setPadding(horizontalPadding, 0, horizontalPadding, 0)
            addView(titleInput)
            addView(bodyInput, LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            ).apply {
                topMargin = (12 * resources.displayMetrics.density).toInt()
            })
        }

        val dialog = AlertDialog.Builder(this)
            .setTitle(if (post == null) "Buat laporan" else "Edit laporan (PUT)")
            .setView(form)
            .setNegativeButton("Batal", null)
            .setPositiveButton("Simpan", null)
            .create()
        dialog.setOnShowListener {
            dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener {
                val title = titleInput.text.toString().trim()
                val body = bodyInput.text.toString().trim()
                if (title.isBlank() || body.isBlank()) {
                    titleInput.error = if (title.isBlank()) "Nama mesin wajib diisi" else null
                    bodyInput.error = if (body.isBlank()) "Deskripsi wajib diisi" else null
                    return@setOnClickListener
                }
                dialog.dismiss()
                if (post == null) createReport(title, body) else updateReport(post.id, title, body)
            }
        }
        dialog.show()
    }

    private fun showPatchForm(post: Post) {
        val titleInput = EditText(this).apply {
            hint = "Nama / kode mesin"
            inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_FLAG_CAP_SENTENCES
            setText(post.title)
            setSingleLine(true)
        }
        val dialog = AlertDialog.Builder(this)
            .setTitle("Ubah judul (PATCH)")
            .setMessage("Deskripsi kerusakan tidak akan berubah.")
            .setView(titleInput)
            .setNegativeButton("Batal", null)
            .setPositiveButton("Simpan", null)
            .create()
        dialog.setOnShowListener {
            dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener {
                val title = titleInput.text.toString().trim()
                if (title.isBlank()) {
                    titleInput.error = "Nama mesin wajib diisi"
                    return@setOnClickListener
                }
                dialog.dismiss()
                runRequest {
                    val response = api.patchPost(post.id, PostRequest(title = title))
                    showResponse(response.code(), response.body() ?: response.errorBody())
                    if (response.isSuccessful) adapter.replace(api.getPosts())
                }
            }
        }
        dialog.show()
    }

    private fun createReport(title: String, body: String) = runRequest {
        val response = api.createPost(PostRequest(title, body))
        showResponse(response.code(), response.body() ?: response.errorBody())
        if (response.isSuccessful) adapter.replace(api.getPosts())
    }

    private fun updateReport(id: String, title: String, body: String) = runRequest {
        val response = api.updatePost(id, PostRequest(title, body))
        showResponse(response.code(), response.body() ?: response.errorBody())
        if (response.isSuccessful) adapter.replace(api.getPosts())
    }

    private fun confirmDelete(post: Post) {
        AlertDialog.Builder(this)
            .setTitle("Hapus laporan?")
            .setMessage("Laporan ${post.title} akan dihapus.")
            .setNegativeButton("Batal", null)
            .setPositiveButton("Hapus") { _, _ ->
                runRequest {
                    val response = api.deletePost(post.id)
                    showResponse(response.code(), response.body() ?: response.errorBody())
                    if (response.isSuccessful) adapter.replace(api.getPosts())
                }
            }
            .show()
    }

    private fun showResponse(code: Int, body: Any?) {
        val json = when (body) {
            is ResponseBody -> body.string()
            null -> ""
            else -> gson.toJson(body)
        }
        val prettyBody = runCatching { gson.toJson(JsonParser.parseString(json)) }
            .getOrDefault(json)
            .ifBlank { "(tanpa body)" }
        AlertDialog.Builder(this)
            .setTitle("HTTP $code")
            .setMessage(prettyBody)
            .setPositiveButton("OK", null)
            .show()
    }

    private fun runRequest(block: suspend () -> Unit) {
        lifecycleScope.launch {
            setLoading(true)
            try {
                block()
            } catch (e: CancellationException) {
                throw e
            } catch (e: IOException) {
                showError("Tidak dapat terhubung. Periksa koneksi internet dan Base URL.")
            } catch (e: HttpException) {
                showError("HTTP ${e.code()}\n${e.response()?.errorBody()?.string().orEmpty()}")
            } catch (e: Exception) {
                showError(e.message ?: "Terjadi kesalahan.")
            } finally {
                setLoading(false)
            }
        }
    }

    private fun setLoading(loading: Boolean) {
        progressBar.visibility = if (loading) View.VISIBLE else View.GONE
        addButton.isEnabled = !loading
        reloadButton.isEnabled = !loading
        reportList.isEnabled = !loading
    }

    private fun showError(message: String) {
        AlertDialog.Builder(this)
            .setTitle("Permintaan gagal")
            .setMessage(message)
            .setPositiveButton("OK", null)
            .show()
    }
}
