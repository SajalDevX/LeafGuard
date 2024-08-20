package com.example.geminiapp

import MainViewModel
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
import com.example.geminiapp.ui.theme.GeminiAppTheme
import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.result.launch
import androidx.activity.viewModels
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController

class MainActivity : ComponentActivity() {
    val viewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            GeminiAppTheme {
                Surface(modifier = Modifier.fillMaxSize()) {
                    NavGraph(viewModel)
                }
            }
        }
    }
}

@Composable
fun NavGraph(viewModel: MainViewModel) {
    val navController = rememberNavController()
    NavHost(
        navController = navController, startDestination = Navs.Home.route
    ) {
        composable(Navs.Home.route) {
            HomeScreen(navController,viewModel)
        }
        composable(Navs.CureScreen.route) {
            CureScreen(viewModel = viewModel)
        }
    }
}


@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun HomeScreen(navController: NavController, authViewModel: MainViewModel) {

    val context = LocalContext.current

    // camera
    var bitmap by remember { mutableStateOf<Bitmap?>(null) }
    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicturePreview()
    ) {
        bitmap = it
    }

    // Gallery
    var imageUri by remember { mutableStateOf<Uri?>(null) }
    var bitmapGallery by remember { mutableStateOf<Bitmap?>(null) }
    var isGallery by remember { mutableStateOf(false) }
    val galleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) {
        imageUri = it
    }

    val gradient = Brush.linearGradient(
        colors = listOf(
            Color(0xFFB7FFF2), // #B7FFF2
            Color(0xFFBAFFDA)  // #BAFFDA
        )
    )

    var expanded by remember { mutableStateOf(false) }
    Scaffold(floatingActionButton = {
        Button(
            onClick = {
                isGallery = true
                galleryLauncher.launch("image/*")
            }, colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFF2EA15C), contentColor = Color.White
            )
        ) {
            Icon(imageVector = Icons.Default.Add, contentDescription = null)
        }
    }, topBar = {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 21.dp, vertical = 15.dp),
            horizontalArrangement = Arrangement.Start
        ) {
            Icon(imageVector = Icons.Default.MoreVert,
                contentDescription = null,
                modifier = Modifier.clickable { expanded = true })
        }
    }) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(text = "Help Your Crop", fontSize = 27.sp, fontWeight = FontWeight.Bold)

            Spacer(modifier = Modifier.height(20.dp))

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp)
                    .height(280.dp)
                    .background(gradient)
                    .clip(RoundedCornerShape(25)),
                contentAlignment = Alignment.Center
            ) {
                Column {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Image(
                                modifier = Modifier
                                    .height(60.dp)
                                    .width(60.dp),
                                painter = painterResource(id = R.drawable.scan),
                                contentDescription = null
                            )
                            Text(text = "Take the \npicture")
                        }

                        Spacer(modifier = Modifier.width(10.dp))

                        Icon(
                            painter = painterResource(id = R.drawable.baseline_keyboard_double_arrow_right_24),
                            contentDescription = null
                        )

                        Column {
                            Image(
                                modifier = Modifier
                                    .height(60.dp)
                                    .width(60.dp),
                                painter = painterResource(id = R.drawable.search),
                                contentDescription = null
                            )
                            Text(text = "Analyse \nDisease")
                        }

                        Spacer(modifier = Modifier.width(10.dp))

                        Icon(
                            painter = painterResource(id = R.drawable.baseline_keyboard_double_arrow_right_24),
                            contentDescription = null
                        )

                        Column {
                            Image(
                                modifier = Modifier
                                    .height(60.dp)
                                    .width(60.dp),
                                painter = painterResource(id = R.drawable.writing),
                                contentDescription = null
                            )
                            Text(text = "Check \n report")
                        }
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    Button(
                        onClick = {
                            launcher.launch()
                        },
                        Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 20.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFF2EA15C), contentColor = Color.White
                        )
                    ) {
                        Text(text = "Take Picture")
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Handle the case where the image has been selected from the gallery
            if (isGallery && imageUri != null) {
                imageUri?.let {
                    bitmapGallery = if (Build.VERSION.SDK_INT < 28) {
                        MediaStore.Images.Media.getBitmap(context.contentResolver, it)
                    } else {
                        val source = ImageDecoder.createSource(context.contentResolver, it)
                        ImageDecoder.decodeBitmap(source)
                    }
                }

                Button(
                    onClick = {
                        bitmapGallery?.let { img ->
                            authViewModel.getImage(img)
                            navController.navigate(Navs.CureScreen.route)
                        }
                    }, colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF2EA15C), contentColor = Color.White
                    )
                ) {
                    Text(text = "Send Image")
                }
            } else if (bitmap != null) {
                Button(
                    onClick = {
                        bitmap?.let { img ->
                            authViewModel.getImage(img)
                            navController.navigate(Navs.CureScreen.route)
                        }
                    }, colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF2EA15C), contentColor = Color.White
                    )
                ) {
                    Text(text = "Send Image")
                }
            }
        }
    }
}


sealed class Navs(val route: String) {
    data object Home : Navs("home")
    data object CureScreen : Navs("curescreen")
}

