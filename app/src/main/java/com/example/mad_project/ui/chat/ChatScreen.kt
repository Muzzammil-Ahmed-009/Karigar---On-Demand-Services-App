package com.karigar.app.ui.chat

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import coil.compose.rememberAsyncImagePainter
import com.karigar.app.data.model.ChatMessage
import com.karigar.app.utils.AudioPlayerHelper
import com.karigar.app.utils.AudioRecorderHelper
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun ChatScreen(
    workerName: String,
    orderId: String,
    onBack: () -> Unit,
    onCall: () -> Unit,
    viewModel: ChatViewModel
) {
    val messages by viewModel.messages.collectAsState()

    LaunchedEffect(orderId) {
        viewModel.loadMessages(orderId)
    }

    ChatContent(
        workerName = workerName,
        messages = messages,
        onBack = onBack,
        onCall = onCall,
        onSendMessage = { viewModel.sendMessage(orderId, it) },
        onDeleteMessage = { viewModel.deleteMessage(orderId, it) },
        onEditMessage = { id, text -> viewModel.editMessage(orderId, id, text) }
    )
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun ChatContent(
    workerName: String,
    messages: List<ChatMessage>,
    onBack: () -> Unit,
    onCall: () -> Unit,
    onSendMessage: (ChatMessage) -> Unit,
    onDeleteMessage: (String) -> Unit,
    onEditMessage: (String, String) -> Unit
) {
    val context = LocalContext.current
    var inputText by remember { mutableStateOf("") }
    val listState = rememberLazyListState()
    val coroutineScope = rememberCoroutineScope()

    LaunchedEffect(messages.size) {
        if (messages.isNotEmpty()) {
            listState.animateScrollToItem(messages.size - 1)
        }
    }

    // Helpers
    val audioRecorder = remember { AudioRecorderHelper(context) }
    val audioPlayer = remember { AudioPlayerHelper() }

    // State
    var isRecording by remember { mutableStateOf(false) }
    var recordingDuration by remember { mutableStateOf(0) }
    var currentAudioPath by remember { mutableStateOf<String?>(null) }
    
    // Playback state
    var playingMessageId by remember { mutableStateOf<String?>(null) }

    // Permissions
    val recordAudioPermission = rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) { granted ->
        if (!granted) Toast.makeText(context, "Microphone permission required", Toast.LENGTH_SHORT).show()
    }

    // Media Pickers
    val galleryLauncher = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        uri?.let {
            val timeNow = SimpleDateFormat("hh:mm a", Locale.getDefault()).format(Date())
            onSendMessage(ChatMessage(text = "", isFromUser = true, time = timeNow, mediaUri = it.toString()))
        }
    }

    // Edit & Unsend state
    var selectedMessageForAction by remember { mutableStateOf<ChatMessage?>(null) }
    var editMode by remember { mutableStateOf(false) }
    var messageToEdit by remember { mutableStateOf<ChatMessage?>(null) }

    // Recording Timer
    LaunchedEffect(isRecording) {
        if (isRecording) {
            recordingDuration = 0
            while (true) {
                delay(1000)
                recordingDuration++
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Surface(shape = CircleShape, color = Color.LightGray, modifier = Modifier.size(36.dp)) {
                            Icon(Icons.Default.Person, contentDescription = null, modifier = Modifier.padding(4.dp))
                        }
                        Spacer(Modifier.width(12.dp))
                        Column {
                            Text(workerName, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                            Text("Online", fontSize = 12.sp, color = Color(0xFF4CAF50))
                        }
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onBack) { Icon(Icons.Default.ArrowBack, contentDescription = "Back") }
                },
                actions = {
                    IconButton(onClick = onCall) { Icon(Icons.Default.Call, contentDescription = "Call") }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White)
            )
        },
        containerColor = Color(0xFFE5DDD5) // WhatsApp background color
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            // Chat List
            LazyColumn(
                state = listState,
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp),
                contentPadding = PaddingValues(vertical = 16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(messages) { msg ->
                    ChatBubble(
                        message = msg,
                        isPlaying = playingMessageId == msg.id,
                        onPlayPause = {
                            if (playingMessageId == msg.id) {
                                audioPlayer.stop()
                                playingMessageId = null
                            } else {
                                msg.audioUri?.let { path ->
                                    audioPlayer.stop()
                                    playingMessageId = msg.id
                                    audioPlayer.play(path) {
                                        playingMessageId = null
                                    }
                                }
                            }
                        },
                        onLongPress = {
                            if (msg.isFromUser && !msg.isUnsent) {
                                selectedMessageForAction = msg
                            }
                        }
                    )
                }
            }

            // Input Bar
            Surface(
                color = Color.Transparent,
                modifier = Modifier.padding(8.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.Bottom,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    // Text Input Area
                    Surface(
                        shape = RoundedCornerShape(24.dp),
                        color = Color.White,
                        modifier = Modifier
                            .weight(1f)
                            .padding(end = 8.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            IconButton(onClick = { galleryLauncher.launch("image/*") }) {
                                Icon(Icons.Default.AttachFile, contentDescription = "Attach", tint = Color.Gray)
                            }
                            
                            if (isRecording) {
                                Text(
                                    "Recording... ${String.format("%02d:%02d", recordingDuration / 60, recordingDuration % 60)}",
                                    color = Color.Red,
                                    modifier = Modifier.weight(1f).padding(8.dp)
                                )
                            } else {
                                TextField(
                                    value = inputText,
                                    onValueChange = { inputText = it },
                                    placeholder = { Text(if (editMode) "Editing message..." else "Message") },
                                    colors = TextFieldDefaults.colors(
                                        focusedContainerColor = Color.Transparent,
                                        unfocusedContainerColor = Color.Transparent,
                                        focusedIndicatorColor = Color.Transparent,
                                        unfocusedIndicatorColor = Color.Transparent
                                    ),
                                    modifier = Modifier.weight(1f)
                                )
                            }
                        }
                    }

                    // Send or Mic Button
                    Box(
                        modifier = Modifier
                            .size(48.dp)
                            .clip(CircleShape)
                            .background(Color(0xFF00A884))
                            .pointerInput(inputText) {
                                if (inputText.isEmpty() && !editMode) {
                                    detectTapGestures(
                                        onPress = {
                                            if (ContextCompat.checkSelfPermission(context, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
                                                recordAudioPermission.launch(Manifest.permission.RECORD_AUDIO)
                                                return@detectTapGestures
                                            }
                                            isRecording = true
                                            currentAudioPath = audioRecorder.startRecording()
                                            tryAwaitRelease()
                                            isRecording = false
                                            val finalPath = audioRecorder.stopRecording()
                                            if (finalPath != null && recordingDuration > 0) {
                                                val timeNow = SimpleDateFormat("hh:mm a", Locale.getDefault()).format(Date())
                                                onSendMessage(ChatMessage(text = "", isFromUser = true, time = timeNow, audioUri = finalPath, audioDuration = recordingDuration))
                                            }
                                        }
                                    )
                                }
                            }
                            .clickable {
                                if (inputText.isNotEmpty()) {
                                    val timeNow = SimpleDateFormat("hh:mm a", Locale.getDefault()).format(Date())
                                    if (editMode && messageToEdit != null) {
                                        onEditMessage(messageToEdit!!.id, inputText)
                                        editMode = false
                                        messageToEdit = null
                                    } else {
                                        onSendMessage(ChatMessage(text = inputText, isFromUser = true, time = timeNow))
                                    }
                                    inputText = ""
                                }
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        if (inputText.isNotEmpty() || editMode) {
                            Icon(Icons.AutoMirrored.Filled.Send, contentDescription = "Send", tint = Color.White)
                        } else {
                            Icon(Icons.Default.Mic, contentDescription = "Mic", tint = Color.White)
                        }
                    }
                }
            }
        }
    }

    // Edit/Unsend Modal
    if (selectedMessageForAction != null) {
        ModalBottomSheet(
            onDismissRequest = { selectedMessageForAction = null },
            containerColor = Color.White
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                if (selectedMessageForAction?.text?.isNotEmpty() == true) {
                    ListItem(
                        headlineContent = { Text("Edit Message") },
                        leadingContent = { Icon(Icons.Default.Edit, contentDescription = null) },
                        modifier = Modifier.clickable {
                            inputText = selectedMessageForAction!!.text
                            editMode = true
                            messageToEdit = selectedMessageForAction
                            selectedMessageForAction = null
                        }
                    )
                }
                ListItem(
                    headlineContent = { Text("Delete for Everyone (Unsend)", color = Color.Red) },
                    leadingContent = { Icon(Icons.Default.Delete, contentDescription = null, tint = Color.Red) },
                    modifier = Modifier.clickable {
                        onDeleteMessage(selectedMessageForAction!!.id)
                        selectedMessageForAction = null
                    }
                )
                Spacer(Modifier.height(32.dp))
            }
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun ChatBubble(
    message: ChatMessage,
    isPlaying: Boolean,
    onPlayPause: () -> Unit,
    onLongPress: () -> Unit
) {
    val align = if (message.isFromUser) Alignment.CenterEnd else Alignment.CenterStart
    val bgColor = if (message.isFromUser) Color(0xFFDCF8C6) else Color.White
    val shape = if (message.isFromUser) {
        RoundedCornerShape(12.dp, 0.dp, 12.dp, 12.dp)
    } else {
        RoundedCornerShape(0.dp, 12.dp, 12.dp, 12.dp)
    }

    Box(modifier = Modifier.fillMaxWidth(), contentAlignment = align) {
        Surface(
            color = bgColor,
            shape = shape,
            shadowElevation = 1.dp,
            modifier = Modifier
                .widthIn(min = 80.dp, max = 280.dp)
                .combinedClickable(
                    onClick = {},
                    onLongClick = onLongPress
                )
        ) {
            Column(modifier = Modifier.padding(8.dp)) {
                if (message.isUnsent) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Block, contentDescription = null, modifier = Modifier.size(16.dp), tint = Color.Gray)
                        Spacer(Modifier.width(4.dp))
                        Text("This message was unsent", color = Color.Gray, fontStyle = FontStyle.Italic, fontSize = 14.sp)
                    }
                } else {
                    // Image/Media
                    if (message.mediaUri != null) {
                        Image(
                            painter = rememberAsyncImagePainter(message.mediaUri),
                            contentDescription = "Image",
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(200.dp)
                                .clip(RoundedCornerShape(8.dp)),
                            contentScale = ContentScale.Crop
                        )
                        Spacer(Modifier.height(4.dp))
                    }

                    // Audio
                    if (message.audioUri != null) {
                        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(vertical = 4.dp)) {
                            IconButton(onClick = onPlayPause, modifier = Modifier.size(36.dp)) {
                                Icon(if (isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow, contentDescription = null, tint = Color.DarkGray)
                            }
                            Spacer(Modifier.width(8.dp))
                            AudioWaveform(isPlaying = isPlaying, modifier = Modifier.weight(1f).height(24.dp))
                            Spacer(Modifier.width(8.dp))
                            Text(String.format("%02d:%02d", message.audioDuration / 60, message.audioDuration % 60), fontSize = 12.sp, color = Color.Gray)
                        }
                    }

                    // Text
                    if (message.text.isNotEmpty()) {
                        Text(message.text, fontSize = 15.sp, color = Color.Black)
                    }
                }

                // Metadata
                Row(
                    modifier = Modifier.align(Alignment.End).padding(top = 2.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    if (message.isEdited && !message.isUnsent) {
                        Text("Edited", fontSize = 10.sp, color = Color.Gray)
                        Spacer(Modifier.width(4.dp))
                    }
                    Text(message.time, fontSize = 10.sp, color = Color.Gray)
                    if (message.isFromUser) {
                        Spacer(Modifier.width(4.dp))
                        Icon(Icons.Default.DoneAll, contentDescription = "Read", tint = Color(0xFF34B7F1), modifier = Modifier.size(14.dp))
                    }
                }
            }
        }
    }
}

@Composable
fun AudioWaveform(isPlaying: Boolean, modifier: Modifier = Modifier) {
    // Dynamic waveform animation
    val infiniteTransition = rememberInfiniteTransition(label = "waveform")
    val heights = List(15) { index ->
        val target = if (isPlaying) (10..100).random().toFloat() else 20f
        val animatedHeight by infiniteTransition.animateFloat(
            initialValue = 20f,
            targetValue = target,
            animationSpec = infiniteRepeatable(
                animation = tween(durationMillis = (300..600).random(), easing = FastOutSlowInEasing),
                repeatMode = RepeatMode.Reverse
            ),
            label = "bar_$index"
        )
        animatedHeight
    }

    Canvas(modifier = modifier) {
        val barWidth = 6f
        val space = 4f
        val totalWidth = (barWidth + space) * heights.size
        var startX = (size.width - totalWidth) / 2

        heights.forEach { heightPercent ->
            val barHeight = (heightPercent / 100f) * size.height
            drawRoundRect(
                color = Color.Gray,
                topLeft = Offset(startX, (size.height - barHeight) / 2),
                size = Size(barWidth, barHeight),
                cornerRadius = CornerRadius(barWidth / 2)
            )
            startX += barWidth + space
        }
    }
}


@Preview(showBackground = true)
@Composable
fun ChatScreenPreview() {
    com.karigar.app.ui.theme.KarigarTheme {
        ChatContent(
            workerName = "Ahmed Khan",
            messages = listOf(
                ChatMessage(id = "1", text = "Hello! How can I help you?", isFromUser = false, time = "10:00 AM"),
                ChatMessage(id = "2", text = "I need help with my tap.", isFromUser = true, time = "10:01 AM"),
                ChatMessage(id = "3", text = "Sure, I'll be there in 10 mins.", isFromUser = false, time = "10:02 AM")
            ),
            onBack = {},
            onCall = {},
            onSendMessage = {},
            onDeleteMessage = {},
            onEditMessage = { _, _ -> }
        )
    }
}
