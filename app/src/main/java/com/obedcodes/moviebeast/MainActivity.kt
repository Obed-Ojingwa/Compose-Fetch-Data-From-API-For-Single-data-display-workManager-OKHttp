package com.obedcodes.moviebeast

import android.net.http.HttpResponseCache.install
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.obedcodes.moviebeast.ui.theme.MovieBeastTheme
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.crashlytics.buildtools.reloc.org.apache.http.client.HttpClient
import com.obedcodes.moviebeast.data.Todo
import com.obedcodes.moviebeast.network.NetworkManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.Serializable
import java.net.HttpURLConnection
import java.net.URL

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MovieBeastTheme {
                TodoScreen()
            }
        }
    }
}

// https://jsonplaceholder.typicode.com/todos/1
@Composable
fun TodoScreen() {
    val apiUrl = "https://jsonplaceholder.typicode.com/todos"
    val networkManager = NetworkManager()
    var todo by remember { mutableStateOf<Todo?>(null) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    // Fetch data asynchronously
    LaunchedEffect(Unit) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val fetchedTodo = networkManager.fetchTodo(apiUrl)
                if (fetchedTodo != null) {
                    todo = fetchedTodo
                } else {
                    errorMessage = "Failed to fetch data. Please check your connection."
                }
            } catch (e: Exception) {
                errorMessage = "An unexpected error occurred: ${e.message}"
            }
        }
    }

    // Display UI
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        if (todo != null) {
            // Display the data
            Column {
                Text("User ID: ${todo?.userId}", style = MaterialTheme.typography.titleMedium)
                Text("ID: ${todo?.id}", style = MaterialTheme.typography.titleMedium)
                Text("Title: ${todo?.title}", style = MaterialTheme.typography.bodyLarge)
                Text(
                    "Completed: ${if (todo?.completed == true) "Yes" else "No"}",
                    style = MaterialTheme.typography.bodyLarge
                )
            }
        } else if (errorMessage != null) {
            // Display the error message
            Text(
                text = errorMessage!!,
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodyLarge
            )
        } else {
            // Show a loading spinner while data is being fetched
            CircularProgressIndicator()
        }
    }
}
