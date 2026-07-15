package com.nanheustad.quiz

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.AspectRatioFrameLayout
import androidx.media3.ui.PlayerView

private val Ink = Color(0xFF16204A)
private val Indigo = Color(0xFF5B4CE5)
private val Sky = Color(0xFFEFF4FF)
private val Mint = Color(0xFFDBF8EB)
private val Peach = Color(0xFFFFE9CF)
private val Gold = Color(0xFFFFC94A)

private data class Lesson(val title: String, val subtitle: String, val emoji: String, val tint: Color)
private data class Quiz(val question: String, val answers: List<String>, val correct: Int)

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent { NanheUstadApp() }
    }
}

@Composable
private fun NanheUstadApp() {
    var page by rememberSaveable { mutableStateOf("splash") }
    MaterialTheme(colorScheme = lightColorScheme(primary = Indigo, secondary = Gold)) {
        when (page) {
            "splash" -> WelcomeSplash { page = "home" }
            "home" -> HomeScreen(onOpenLesson = { page = "lesson" })
            "lesson" -> LessonScreen(onBack = { page = "home" }, onQuiz = { page = "quiz" })
            else -> QuizScreen(onHome = { page = "home" })
        }
    }
}

@Composable
private fun HomeScreen(onOpenLesson: () -> Unit) {
    val categories = listOf("अक्षर", "Numbers", "कहानियाँ", "Rhymes")
    val continueWatching = listOf(
        Lesson("चलो गिनती सीखें", "Numbers • 4 min", "🔢", Peach),
        Lesson("मेरा पहला अक्षर", "Hindi • 3 min", "अ", Mint),
        Lesson("रंगों की दुनिया", "Fun learning • 5 min", "🎨", Sky)
    )
    val stories = listOf(
        Lesson("शेर और चूहा", "मज़ेदार कहानी", "🦁", Peach),
        Lesson("नन्ही चिड़िया", "सोने से पहले", "🐦", Mint),
        Lesson("जादुई पेड़", "नई कहानी", "🌳", Sky)
    )
    Scaffold(bottomBar = { BottomBar("home", onOpenLesson) }) { padding ->
        Column(
            modifier = Modifier.fillMaxSize().background(Color.White).padding(padding).verticalScroll(rememberScrollState()).padding(bottom = 18.dp)
        ) {
            Row(Modifier.fillMaxWidth().padding(20.dp), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                Column { Text("नमस्ते, नन्हे उस्ताद!", fontSize = 22.sp, fontWeight = FontWeight.Bold, color = Ink); Text("आज क्या सीखना है?", color = Color(0xFF63708D)) }
                Surface(color = Sky, shape = RoundedCornerShape(18.dp)) { Text("👦", fontSize = 24.sp, modifier = Modifier.padding(9.dp)) }
            }
            HeroLesson(onOpenLesson)
            Spacer(Modifier.height(20.dp))
            SectionTitle("सीखने के लिए चुनें")
            LazyRow(contentPadding = PaddingValues(horizontal = 20.dp), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                items(categories) { category -> Surface(shape = RoundedCornerShape(18.dp), color = Sky) { Text(category, modifier = Modifier.padding(horizontal = 16.dp, vertical = 11.dp), color = Ink, fontWeight = FontWeight.Medium) } }
            }
            Spacer(Modifier.height(20.dp))
            SectionTitle("फिर से देखें")
            LessonRail(continueWatching, onOpenLesson)
            Spacer(Modifier.height(20.dp))
            SectionTitle("कहानियों का घर")
            LessonRail(stories, onOpenLesson)
        }
    }
}

@Composable
private fun HeroLesson(onOpenLesson: () -> Unit) {
    Surface(modifier = Modifier.padding(horizontal = 20.dp).fillMaxWidth().clickable(onClick = onOpenLesson), shape = RoundedCornerShape(28.dp), color = Indigo) {
        Row(modifier = Modifier.heightIn(min = 165.dp).padding(18.dp), verticalAlignment = Alignment.CenterVertically) {
            Column(modifier = Modifier.weight(1f)) {
                Text("Genie Teacher की आज की class", color = Color.White.copy(alpha = .8f), fontSize = 13.sp)
                Spacer(Modifier.height(6.dp))
                Text("चलो गिनती सीखें", color = Color.White, fontSize = 23.sp, fontWeight = FontWeight.Bold)
                Text("देखो, गाओ और खेलो", color = Color.White.copy(alpha = .88f))
                Spacer(Modifier.height(14.dp))
                Surface(shape = RoundedCornerShape(14.dp), color = Gold) { Text("▶ अभी देखें", modifier = Modifier.padding(horizontal = 13.dp, vertical = 8.dp), color = Ink, fontWeight = FontWeight.Bold) }
            }
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Image(painter = painterResource(R.drawable.genie_reference), contentDescription = "Genie Teacher", modifier = Modifier.width(105.dp).height(118.dp).clip(RoundedCornerShape(22.dp)), contentScale = ContentScale.Crop)
                Text("Genie Teacher", color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(top = 4.dp))
            }
        }
    }
}

@Composable
private fun SectionTitle(text: String) { Text(text, modifier = Modifier.padding(horizontal = 20.dp, vertical = 9.dp), fontSize = 19.sp, fontWeight = FontWeight.Bold, color = Ink) }

@Composable
private fun LessonRail(lessons: List<Lesson>, onOpenLesson: () -> Unit) {
    LazyRow(contentPadding = PaddingValues(horizontal = 20.dp), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
        items(lessons) { lesson ->
            Surface(modifier = Modifier.width(158.dp).clickable(onClick = onOpenLesson), color = Color.White, shape = RoundedCornerShape(20.dp), border = BorderStroke(1.dp, Color(0xFFE5E9F4))) {
                Column {
                    Box(modifier = Modifier.fillMaxWidth().height(105.dp).background(lesson.tint), contentAlignment = Alignment.Center) { Text(lesson.emoji, fontSize = 45.sp) }
                    Text(lesson.title, modifier = Modifier.padding(start = 12.dp, end = 12.dp, top = 11.dp), color = Ink, fontWeight = FontWeight.Bold, maxLines = 1)
                    Text(lesson.subtitle, modifier = Modifier.padding(start = 12.dp, end = 12.dp, top = 3.dp, bottom = 12.dp), color = Color(0xFF63708D), fontSize = 12.sp)
                }
            }
        }
    }
}

@Composable
private fun LessonScreen(onBack: () -> Unit, onQuiz: () -> Unit) {
    Scaffold(bottomBar = { BottomBar("learn", onBack) }) { padding ->
        Column(Modifier.fillMaxSize().background(Color.White).padding(padding).verticalScroll(rememberScrollState())) {
            Row(Modifier.fillMaxWidth().padding(14.dp), verticalAlignment = Alignment.CenterVertically) { TextButton(onClick = onBack) { Text("‹ वापस", color = Ink) }; Text("Numbers सीखें", color = Ink, fontWeight = FontWeight.Bold) }
            LessonVideo()
            Column(Modifier.padding(20.dp)) {
                Text("चलो गिनती सीखें", fontSize = 25.sp, color = Ink, fontWeight = FontWeight.Bold)
                Text("Numbers • 4 मिनट", color = Color(0xFF63708D), modifier = Modifier.padding(top = 4.dp))
                Spacer(Modifier.height(18.dp))
                Text("इस वीडियो में", color = Ink, fontWeight = FontWeight.Bold, fontSize = 18.sp)
                Text("• 1 से 10 तक गिनती\n• चीज़ों को गिनने का मज़ेदार तरीका\n• छोटी rhyme के साथ practice", color = Color(0xFF44506A), modifier = Modifier.padding(top = 8.dp), lineHeight = 24.sp)
                Spacer(Modifier.height(24.dp))
                Button(onClick = onQuiz, modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(16.dp), colors = ButtonDefaults.buttonColors(containerColor = Indigo)) { Text("जादुई क्विज़ खेलो  ✨", modifier = Modifier.padding(vertical = 4.dp), fontWeight = FontWeight.Bold) }
            }
        }
    }
}

@Composable
private fun LessonVideo() {
    val context = LocalContext.current
    val exoPlayer = remember {
        ExoPlayer.Builder(context).build().apply { setMediaItem(MediaItem.fromUri("android.resource://${context.packageName}/${R.raw.welcome_genie}")); prepare(); playWhenReady = false }
    }
    DisposableEffect(exoPlayer) { onDispose { exoPlayer.release() } }
    AndroidView(modifier = Modifier.fillMaxWidth().height(265.dp).background(Ink), factory = { PlayerView(it).apply { player = exoPlayer; resizeMode = AspectRatioFrameLayout.RESIZE_MODE_ZOOM } })
}

@Composable
private fun QuizScreen(onHome: () -> Unit) {
    val quiz = Quiz("कौन सा फल लाल रंग का होता है?", listOf("🍎  सेब", "🍌  केला", "🥭  आम"), 0)
    var selected by remember { mutableIntStateOf(-1) }
    Scaffold(bottomBar = { BottomBar("learn", onHome) }) { padding ->
        Column(Modifier.fillMaxSize().background(Sky).padding(padding).padding(20.dp), horizontalAlignment = Alignment.CenterHorizontally) {
            Text("जादुई क्विज़", fontSize = 23.sp, fontWeight = FontWeight.Bold, color = Ink)
            Text("वीडियो के बाद एक छोटा सवाल", color = Color(0xFF63708D))
            Spacer(Modifier.height(22.dp))
            Image(painter = painterResource(R.drawable.genie_reference), contentDescription = "Genie asks a question", modifier = Modifier.height(240.dp).fillMaxWidth(.72f).clip(RoundedCornerShape(28.dp)), contentScale = ContentScale.Crop)
            Spacer(Modifier.height(15.dp))
            Surface(shape = RoundedCornerShape(24.dp), color = Color.White) {
                Column(Modifier.padding(18.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(quiz.question, color = Ink, fontSize = 20.sp, textAlign = TextAlign.Center, fontWeight = FontWeight.Bold)
                    quiz.answers.forEachIndexed { i, answer ->
                        val fill = when { selected == i && i == quiz.correct -> Mint; selected == i -> Color(0xFFFFE1E1); else -> Color(0xFFF8F9FC) }
                        Surface(modifier = Modifier.fillMaxWidth().padding(top = 10.dp).clickable { if (selected == -1) selected = i }, shape = RoundedCornerShape(15.dp), color = fill, border = BorderStroke(1.dp, Color(0xFFE5E9F4))) { Text(answer, modifier = Modifier.padding(14.dp), textAlign = TextAlign.Center, color = Ink, fontWeight = FontWeight.Medium) }
                    }
                    if (selected >= 0) Text(if (selected == quiz.correct) "शाबाश! आपने स्टार जीता ✨" else "कोई बात नहीं, फिर कोशिश करें!", modifier = Modifier.padding(top = 14.dp), color = if (selected == quiz.correct) Color(0xFF087A51) else Color(0xFFB42318), fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@Composable
private fun WelcomeSplash(onDone: () -> Unit) {
    val context = LocalContext.current
    val player = remember { ExoPlayer.Builder(context).build().apply { setMediaItem(MediaItem.fromUri("android.resource://${context.packageName}/${R.raw.welcome_genie}")); prepare(); playWhenReady = true } }
    DisposableEffect(player) {
        val listener = object : Player.Listener { override fun onPlaybackStateChanged(state: Int) { if (state == Player.STATE_ENDED) onDone() } }
        player.addListener(listener)
        onDispose { player.removeListener(listener); player.release() }
    }
    Box(Modifier.fillMaxSize().background(Ink)) {
        AndroidView(modifier = Modifier.fillMaxSize(), factory = { PlayerView(it).apply { useController = false; this.player = player; resizeMode = AspectRatioFrameLayout.RESIZE_MODE_ZOOM } })
        Column(Modifier.align(Alignment.BottomCenter).padding(28.dp), horizontalAlignment = Alignment.CenterHorizontally) { Text("नन्हे उस्ताद", color = Color.White, fontSize = 30.sp, fontWeight = FontWeight.Bold); Text("सीखो, खेलो और चमको!", color = Color.White); Spacer(Modifier.height(14.dp)); TextButton(onClick = onDone, colors = ButtonDefaults.textButtonColors(contentColor = Color.White)) { Text("आगे बढ़ें  ›", fontWeight = FontWeight.Bold) } }
    }
}

@Composable
private fun BottomBar(active: String, onHome: () -> Unit) {
    Surface(color = Color.White, shadowElevation = 8.dp) {
        Row(Modifier.fillMaxWidth().padding(vertical = 8.dp), horizontalArrangement = Arrangement.SpaceEvenly) {
            BottomItem("⌂", "होम", active == "home", onHome)
            BottomItem("▶", "सीखें", active == "learn", onHome)
            BottomItem("★", "मेरे स्टार", false, onHome)
            BottomItem("☺", "पेरेंट्स", false, onHome)
        }
    }
}

@Composable
private fun BottomItem(icon: String, label: String, active: Boolean, onClick: () -> Unit) {
    Column(modifier = Modifier.clip(RoundedCornerShape(14.dp)).clickable(onClick = onClick).padding(horizontal = 8.dp, vertical = 4.dp), horizontalAlignment = Alignment.CenterHorizontally) { Text(icon, color = if (active) Indigo else Color(0xFF63708D), fontSize = 20.sp); Text(label, color = if (active) Indigo else Color(0xFF63708D), fontSize = 11.sp, fontWeight = if (active) FontWeight.Bold else FontWeight.Normal) }
}
