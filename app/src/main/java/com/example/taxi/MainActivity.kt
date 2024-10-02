package com.example.taxi
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.rememberCameraPositionState
import com.google.android.gms.maps.model.LatLng
import kotlinx.coroutines.delay
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.foundation.text.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.runtime.mutableStateOf
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.clickable
import androidx.compose.material3.ButtonDefaults
import com.google.maps.android.compose.MarkerState
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.res.painterResource
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.maps.android.compose.Marker
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.platform.LocalContext
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.Manifest
import android.content.pm.PackageManager
import android.location.Location
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import com.google.android.gms.location.LocationServices
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import com.google.android.gms.maps.CameraUpdateFactory
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import com.google.accompanist.permissions.*
import android.app.Activity
import android.content.Context
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.unit.dp
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.rememberCameraPositionState
import com.google.maps.android.compose.Polyline
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query
import android.widget.Toast











class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MyApp()
        }
    }
}

@Composable
fun MyApp() {
    // Состояние для текущего экрана
    var currentScreen by remember { mutableStateOf<Screen>(Screen.LaunchScreen) }

    // Выбор экрана на основе текущего состояния
    when (currentScreen) {
        Screen.LaunchScreen -> LaunchScreen {
            // Переход на экран входа
            currentScreen = Screen.SignIn
        }
        Screen.SignIn -> SignInScreen(
            onSignInSuccess = {
                // Переход на главный экран после успешного входа
                currentScreen = Screen.StartScreen
            },
            onLoginClick = {
                // Переход на экран регистрации
                currentScreen = Screen.SignUp
            }
        )
        Screen.SignUp -> SignUpScreen(
            onSignUpSuccess = {
                // Переход на главный экран после успешной регистрации
                currentScreen = Screen.StartScreen
            },
            onLoginClick = {
                // Переход на экран входа
                currentScreen = Screen.SignIn
            }
        )
        Screen.StartScreen -> StartScreen {
            // Переход на экран Меню
            currentScreen = Screen.Menus
        }
        Screen.Menus -> MenusScreen {
            // Переход на экран Истории
            currentScreen = Screen.History
        }
        Screen.History -> HistoryScreen {
            // Переход на экран Настроек
            currentScreen = Screen.Settings
        }
        Screen.Settings -> SettingsScreen {
            // Переход на экран входа (можно изменить, если необходимо)
            currentScreen = Screen.SignIn
        }
    }
}




// Список экранов
sealed class Screen {
    object LaunchScreen : Screen()
    object SignIn : Screen()
    object SignUp : Screen()
    object StartScreen : Screen()
    object Menus : Screen()
    object History : Screen()
    object Settings : Screen()
}

// LaunchScreen - отображается только пока загружается приложение
@Composable
fun LaunchScreen(onLaunchComplete: () -> Unit) {
    LaunchedEffect(Unit) {
        delay(3000) // Эмуляция загрузки приложения (3 секунды)
        onLaunchComplete()
    }
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF1E1E1E)),
        contentAlignment = Alignment.Center
    ) {
        Image(
            painter = painterResource(id = R.drawable.launchscreen),
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop)
    }
}

// SignInScreen - проверка пустых полей
@Composable
fun SignInScreen(onSignInSuccess: () -> Unit, onLoginClick: () -> Unit) {
    var login by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var showError by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF1E1E1E)),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            TextField(
                value = login,
                onValueChange = { login = it },
                label = { Text("login") },
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = Color.White,
                    unfocusedContainerColor = Color.White
                )
            )
            Spacer(modifier = Modifier.height(16.dp))
            TextField(
                value = password,
                onValueChange = { password = it },
                label = { Text("*****") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = Color.White,
                    unfocusedContainerColor = Color.White
                )
            )
            Spacer(modifier = Modifier.height(16.dp))

            // Добавляем текст "Забыли пароль?"
            Text(
                text = "Forgot Password?",
                color = Color.White,
                modifier = Modifier
                    .clickable {
                        // Здесь вы можете добавить логику для обработки забытого пароля
                    }
                    .padding(bottom = 16.dp) // Отступ перед кнопкой
            )

            Button(colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF3F425C)),
                onClick = {
                if (login.isNotEmpty() && password.isNotEmpty()) {
                    onSignInSuccess()
                } else {
                    showError = true
                }
            }) {
                Text("SIGN IN", color = Color.White)
            }
            if (showError) {
                Text("Пожалуйста, заполните все поля", color = Color.Red)
            }
            Spacer(modifier = Modifier.height(16.dp)) // Отступ перед текстом для регистрации
            Text(
                text = "Create A New Account?",
                color = Color.White,
                modifier = Modifier
                    .clickable { onLoginClick() } // Используем onLoginClick
            )
        }
    }
}



// SignUpScreen - проверка пустых полей




// StartScreen - реальная карта с маркерами
@Composable
fun StartScreen(onMenuClick: () -> Unit) {
    val novosibirsk = LatLng(55.0084, 82.9357)
    val ДЖАМАЛЬ = LatLng(55.048377, 82.923396)
    val Алексей = LatLng(55.045510, 82.928608)
    val Иван = LatLng(55.038529, 82.941892)
    val Дима = LatLng(55.041812, 82.912897)
    val cameraPositionState = rememberCameraPositionState {
        position = com.google.android.gms.maps.model.CameraPosition.fromLatLngZoom(novosibirsk, 12f)
    }

    // Получаем контекст
    val context = LocalContext.current

    // Инициализация FusedLocationProviderClient
    val fusedLocationClient = remember { LocationServices.getFusedLocationProviderClient(context) }
    var currentLocation by remember { mutableStateOf<LatLng?>(null) }
    var polylinePoints by remember { mutableStateOf<List<LatLng>>(emptyList()) }

    // Запрашиваем разрешения
    val permissions = arrayOf(
        Manifest.permission.ACCESS_FINE_LOCATION,
        Manifest.permission.ACCESS_COARSE_LOCATION
    )
    val permissionGranted = permissions.all {
        context.checkSelfPermission(it) == PackageManager.PERMISSION_GRANTED
    }

    LaunchedEffect(Unit) {
        if (permissionGranted) {
            // Получаем последнее местоположение
            fusedLocationClient.lastLocation.addOnSuccessListener { location ->
                location?.let {
                    currentLocation = LatLng(it.latitude, it.longitude)
                    // Перемещаем камеру на текущее местоположение
                    currentLocation?.let { latLng ->
                        cameraPositionState.move(CameraUpdateFactory.newLatLngZoom(latLng, 15f))
                    }
                }
            }
        } else {
            // Запрашиваем разрешения
            ActivityCompat.requestPermissions(
                (context as Activity),
                permissions,
                1000 // ID запроса разрешения
            )
        }
    }

    // Загружаем изображение как Bitmap
    val markerBitmap = remember { BitmapFactory.decodeResource(context.resources, R.drawable.car) }

    // Список маркеров
    val markers = listOf(ДЖАМАЛЬ, Алексей, Иван, Дима)

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        GoogleMap(
            modifier = Modifier.fillMaxSize(),
            cameraPositionState = cameraPositionState
        ) {
            // Добавление маркеров
            markers.forEachIndexed { index, position ->
                Marker(
                    state = MarkerState(position = position),
                    title = "Водитель:$index",
                    snippet = "Таксист",
                    icon = BitmapDescriptorFactory.fromBitmap(markerBitmap)
                )
            }

            // Добавление маркера для текущего местоположения
            currentLocation?.let { latLng ->
                Marker(
                    state = MarkerState(position = latLng),
                    title = "Мое местоположение",
                    snippet = "Вы находитесь здесь",
                    icon = BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE) // Используем стандартный маркер
                )
            }

            // Рисуем полилинию, если есть
            if (polylinePoints.isNotEmpty()) {
                Polyline(
                    points = polylinePoints,
                    color = Color.Blue,
                    width = 5f
                )
            }
        }

        // Кнопка для расчета расстояния и построения маршрута


    }
}











// Остальные экраны
@Composable
fun MenusScreen(onHistoryClick: () -> Unit) {
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text("Меню")
        Spacer(modifier = Modifier.height(16.dp))
        Button(onClick = onHistoryClick) {
            Text("История")
        }
    }
}

@Composable
fun SignUpScreen(onSignUpSuccess: () -> Unit, onLoginClick: () -> Unit) {
    var login by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var showError by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF1E1E1E)),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(16.dp)
        ) {
            TextField(
                value = login,
                onValueChange = { login = it },
                label = { Text("login") },
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = Color.White,
                    unfocusedContainerColor = Color.White
                )
            )
            Spacer(modifier = Modifier.height(16.dp))
            TextField(
                value = email,
                onValueChange = { email = it },
                label = { Text("Email") },
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = Color.White,
                    unfocusedContainerColor = Color.White
                )
            )
            Spacer(modifier = Modifier.height(16.dp))
            TextField(
                value = password,
                onValueChange = { password = it },
                label = { Text("*****") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = Color.White,
                    unfocusedContainerColor = Color.White
                )
            )
            Spacer(modifier = Modifier.height(16.dp))
            TextField(
                value = confirmPassword,
                onValueChange = { confirmPassword = it },
                label = { Text("*****") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = Color.White,
                    unfocusedContainerColor = Color.White
                )
            )
            Spacer(modifier = Modifier.height(16.dp))
            Button(colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF3F425C)),
                onClick = {
                if (login.isNotEmpty() && email.isNotEmpty() && password.isNotEmpty() && confirmPassword.isNotEmpty()) {
                    if (password == confirmPassword) { // Проверка на совпадение паролей
                        onSignUpSuccess() // Переход на экран SignIn
                    } else {
                        showError = true
                    }
                } else {
                    showError = true
                }
            }) {
                Text("SIGN UP", color = Color.White)
            }
            if (showError) {
                Text("Пожалуйста, заполните все поля и убедитесь, что пароли совпадают", color = Color.Red)
            }
            Spacer(modifier = Modifier.height(16.dp))
            // Текстовая ссылка для перехода на экран входа

        }
    }
}





@Composable
fun HistoryScreen(onSettingsClick: () -> Unit) {
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text("История поездок")
        Spacer(modifier = Modifier.height(16.dp))
        Button(onClick = onSettingsClick) {
            Text("Настройки")
        }
    }
}

@Composable
fun SettingsScreen(onExitClick: () -> Unit) {
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text("Настройки пользователя")
        Spacer(modifier = Modifier.height(16.dp))
        Button(onClick = onExitClick) {
            Text("Выйти")
        }
    }
}

