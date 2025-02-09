package com.example.artspace

import android.content.Context
import android.content.res.Configuration
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.rememberAsyncImagePainter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

@Composable
fun ArtPieceScreen(artPiece: ArtPiece, onNext: () -> Unit, onBack: () -> Unit) {
    val configuration = LocalConfiguration.current
    val isPortrait = configuration.orientation == Configuration.ORIENTATION_PORTRAIT
    val buttonSize = 60.dp

    val playfairFont = FontFamily(
        Font(R.font.playfair_display, FontWeight.Normal),
    )

    var hasSwiped by remember { mutableStateOf(false) }
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = if (isPortrait) Arrangement.Center else Arrangement.Top,
        modifier = Modifier
            .fillMaxSize()
            .pointerInput(Unit) {
                detectHorizontalDragGestures(
                    onDragStart = { hasSwiped = false },
                    onHorizontalDrag = { _, dragAmount ->
                        if (!hasSwiped) {
                            if (dragAmount < -100) {
                                onNext()
                                hasSwiped = true
                            }
                            if (dragAmount > 100) {
                                onBack()
                                hasSwiped = true
                            }
                        }
                    }
                )
            }
    ) {
        Text(
            text = "ArtSpace",
            fontWeight = FontWeight.Bold,
            fontFamily = playfairFont,
            style = MaterialTheme.typography.titleLarge,
            modifier = Modifier.padding(16.dp)
        )
        Box(
            modifier = Modifier
                .shadow(8.dp, shape = MaterialTheme.shapes.medium)
                .background(Color.White)
                .width(if (isPortrait) 300.dp else 200.dp)
        ) {
            Image(
                painter = rememberAsyncImagePainter(artPiece.imageUrl),
                contentDescription = "ArtPiece Image",
                modifier = Modifier
                    .fillMaxWidth()
                    .height(if (isPortrait) 300.dp else 200.dp),
                contentScale = ContentScale.Fit
            )
        }


        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = artPiece.title,
            fontWeight = FontWeight.Bold,
            style = MaterialTheme.typography.titleMedium,
            fontFamily = playfairFont,
            fontSize = 20.sp,
            modifier = Modifier.align(Alignment.CenterHorizontally)
        )
        Text(
            text = artPiece.author,
            style = MaterialTheme.typography.bodyLarge,
            fontFamily = playfairFont,
            fontSize = 20.sp
        )

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
                .height(120.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.Bottom
        ) {
            Button(
                onClick = onBack,
                modifier = Modifier
                    .padding(10.dp)
                    .height(buttonSize),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.Black,
                    contentColor = Color.White
                )
            ) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Назад")
            }

            Button(
                onClick = onNext,
                modifier = Modifier
                    .padding(10.dp)
                    .height(buttonSize),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.Black,
                    contentColor = Color.White
                )
            ) {
                Icon(Icons.AutoMirrored.Filled.ArrowForward, contentDescription = "Далее")
            }
        }

    }
}

@Composable
fun ArtGalleryScreen(artPieces: List<ArtPiece>, startIndex: Int) {
    var currentIndex by rememberSaveable { mutableIntStateOf(startIndex) }

    val onNext: () -> Unit = {
        if (currentIndex < artPieces.size - 1) {
            currentIndex++
        }
    }

    val onBack: () -> Unit = {
        if (currentIndex > 0) {
            currentIndex--
        }
    }

    ArtPieceScreen(artPiece = artPieces[currentIndex], onNext = onNext, onBack = onBack)
}


fun loadArtPieces(context: Context): List<ArtPiece> {
    val jsonString = context.assets.open("art_pieces.json").bufferedReader().use { it.readText() }
    val listType = object : TypeToken<List<ArtPiece>>() {}.type
    return Gson().fromJson(jsonString, listType)
}

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val artPieces = loadArtPieces(this)
        setContent {
            ArtGalleryScreen(artPieces = artPieces, startIndex = 0)
        }
    }
}

data class ArtPiece(val title: String, val author: String, val imageUrl: String)
