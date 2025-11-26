package com.example.myapplication.features.profile

import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.myapplication.R
import com.example.myapplication.ui.theme.darkBlue


val lightBlue = Color(0xFFE3F2FD)
val greyText = Color(0xFF757575)
val dividerGrey = Color(0xFFE0E0E0)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    userName: String = "User",
    userEmail: String = "",
    userProfileImageUrl: String? = null,
    showProfilePicture: Boolean = false,
    onBack: () -> Unit = {},
    onCreateCV: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val selectedCvOption = remember { mutableStateOf("Upload CV") }
    val selectedCvFile = remember { mutableStateOf<String?>(null) }
    
    // Profile data state - initialize with user info from Firebase
    var name by remember { mutableStateOf(userName) }
    var phone by remember { mutableStateOf("01xxxxxxx") }
    var email by remember { mutableStateOf(userEmail) }
    var skills by remember { mutableStateOf("Python, JavaScript,") }
    var experience by remember { mutableStateOf("2 years as Junior ...") }
    
    // Update when user info changes
    androidx.compose.runtime.LaunchedEffect(userName, userEmail) {
        name = userName
        email = userEmail
    }
    
    // Edit states for each field
    var isEditingName by remember { mutableStateOf(false) }
    var isEditingPhone by remember { mutableStateOf(false) }
    var isEditingEmail by remember { mutableStateOf(false) }
    var isEditingSkills by remember { mutableStateOf(false) }
    var isEditingExperience by remember { mutableStateOf(false) }
    
    // File picker launcher
    val filePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri ->
        uri?.let {
            selectedCvFile.value = it.toString()
        }
    }
    
    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri ->
        uri?.let {
            Toast.makeText(context, "Photo selected", Toast.LENGTH_SHORT).show()
        }
    }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { 
                    Text(
                        "Profile", 
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    ) 
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            Icons.Default.ArrowBack,
                            contentDescription = "Back",
                            tint = Color.White
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = darkBlue,
                    titleContentColor = Color.White,
                    navigationIconContentColor = Color.White
                )
            )
        }
    ) { innerPadding ->
        Column(
            modifier = modifier
                .fillMaxSize()
                .background(Color.White)
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
        ) {
            // Profile Picture Section
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Box(
                    modifier = Modifier.size(120.dp)
                ) {
                    // Show Google profile picture if available, otherwise show placeholder
                    if (showProfilePicture && userProfileImageUrl != null) {
                        // Google sign in - show Google profile picture
                        AsyncImage(
                            model = userProfileImageUrl,
                            contentDescription = "Profile Picture",
                            modifier = Modifier
                                .size(120.dp)
                                .clip(CircleShape),
                            placeholder = painterResource(id = R.drawable.shruti),
                            error = painterResource(id = R.drawable.shruti)
                        )
                    } else {
                        // Email/password sign in - show placeholder (user can set photo manually)
                        Box(
                            modifier = Modifier
                                .size(120.dp)
                                .clip(CircleShape)
                                .background(lightBlue),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                Icons.Default.Edit,
                                contentDescription = "Profile Picture",
                                tint = greyText,
                                modifier = Modifier.size(60.dp)
                            )
                        }
                    }
                    // Edit button overlay - positioned at 5 o'clock (bottom-right, half overlapping)
                    // Profile is 120dp, edit icon is 36dp. To have half overlap: center should be at edge
                    // Using offset to position at 5 o'clock: slight right and down from bottom-end
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .background(darkBlue, CircleShape)
                            .align(Alignment.BottomEnd)
                            .offset(x = 6.dp, y = 6.dp) // Position at 5 o'clock: half overlaps, half extends
                            .clickable { imagePickerLauncher.launch("image/*") },
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            Icons.Default.Edit,
                            contentDescription = "Edit Photo",
                            tint = Color.White,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            }
            
            // Divider
            Spacer(modifier = Modifier.height(8.dp))
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(1.dp)
                    .background(dividerGrey)
            )
            
            // Basic Information Section
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 16.dp)
            ) {
                Text(
                    text = "Basic Information",
                    fontSize = 16.sp,
                    color = greyText,
                    modifier = Modifier.padding(bottom = 16.dp)
                )
                
                // Name Field
                EditableInfoField(
                    label = "Name",
                    value = name,
                    onValueChange = { name = it },
                    isEditing = isEditingName,
                    onEditClick = { isEditingName = !isEditingName }
                )
                
                Spacer(modifier = Modifier.height(12.dp))
                
                // Phone Field
                EditableInfoField(
                    label = "Phone",
                    value = phone,
                    onValueChange = { phone = it },
                    isEditing = isEditingPhone,
                    onEditClick = { isEditingPhone = !isEditingPhone }
                )
                
                Spacer(modifier = Modifier.height(12.dp))
                
                // Email Field
                EditableInfoField(
                    label = "Email",
                    value = email,
                    onValueChange = { email = it },
                    isEditing = isEditingEmail,
                    onEditClick = { isEditingEmail = !isEditingEmail }
                )
                
                Spacer(modifier = Modifier.height(12.dp))
                
                // Skills Field
                EditableInfoField(
                    label = "Skills",
                    value = skills,
                    onValueChange = { skills = it },
                    isEditing = isEditingSkills,
                    onEditClick = { isEditingSkills = !isEditingSkills }
                )
                
                Spacer(modifier = Modifier.height(12.dp))
                
                // Experience Field
                EditableInfoField(
                    label = "Experience",
                    value = experience,
                    onValueChange = { experience = it },
                    isEditing = isEditingExperience,
                    onEditClick = { isEditingExperience = !isEditingExperience }
                )
            }
            
            // Divider
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(1.dp)
                    .background(dividerGrey)
            )
            
            // CV and Resume Section
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 16.dp)
            ) {
                Text(
                    text = "CV and Resume",
                    fontSize = 16.sp,
                    color = greyText,
                    modifier = Modifier.padding(bottom = 16.dp)
                )
                
                // Upload CV Option
                Column {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.clickable { selectedCvOption.value = "Upload CV" }
                    ) {
                        RadioButton(
                            selected = selectedCvOption.value == "Upload CV",
                            onClick = { selectedCvOption.value = "Upload CV" },
                            colors = RadioButtonDefaults.colors(
                                selectedColor = Color.Black
                            )
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Upload CV",
                            fontSize = 16.sp,
                            color = Color.Black
                        )
                    }
                    
                    if (selectedCvOption.value == "Upload CV") {
                        Spacer(modifier = Modifier.height(12.dp))
                        OutlinedButton(
                            onClick = { filePickerLauncher.launch("*/*") },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text("Choose File")
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = selectedCvFile.value?.let { "File selected: ${it.substringAfterLast("/")}" } ?: "No file chosen",
                            fontSize = 14.sp,
                            color = if (selectedCvFile.value != null) Color(0xFF4CAF50) else greyText
                        )
                    }
                }
                
                Spacer(modifier = Modifier.height(8.dp))
                
                // Create CV Option
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.clickable { 
                        selectedCvOption.value = "Create CV"
                        onCreateCV()
                    }
                ) {
                    RadioButton(
                        selected = selectedCvOption.value == "Create CV",
                        onClick = { 
                            selectedCvOption.value = "Create CV"
                            onCreateCV()
                        },
                        colors = RadioButtonDefaults.colors(
                            selectedColor = Color.Black
                        )
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Create CV",
                        fontSize = 16.sp,
                        color = Color.Black
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(32.dp))
            
            // Update Button
            Button(
                onClick = {
                    // Reset edit states
                    isEditingName = false
                    isEditingPhone = false
                    isEditingEmail = false
                    isEditingSkills = false
                    isEditingExperience = false
                    // Show success toast
                    Toast.makeText(context, "Update Successful", Toast.LENGTH_SHORT).show()
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
                    .height(56.dp),
                shape = RoundedCornerShape(12.dp),
                colors = androidx.compose.material3.ButtonDefaults.buttonColors(
                    containerColor = darkBlue
                )
            ) {
                Text(
                    text = "Update",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }
            
            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
private fun EditableInfoField(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    isEditing: Boolean,
    onEditClick: () -> Unit
) {
    Column {
        Text(
            text = label,
            fontSize = 14.sp,
            color = Color.Black,
            modifier = Modifier.padding(bottom = 4.dp)
        )
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (isEditing) {
                OutlinedTextField(
                    value = value,
                    onValueChange = onValueChange,
                    modifier = Modifier.weight(1f),
                    singleLine = true
                )
            } else {
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .background(lightBlue, RoundedCornerShape(8.dp))
                        .padding(horizontal = 12.dp, vertical = 14.dp)
                ) {
                    Text(
                        text = value,
                        fontSize = 14.sp,
                        color = Color.Black
                    )
                }
            }
            Spacer(modifier = Modifier.width(8.dp))
            IconButton(
                onClick = onEditClick,
                modifier = Modifier.size(40.dp)
            ) {
                Icon(
                    Icons.Default.Edit,
                    contentDescription = "Edit",
                    tint = Color.Black,
                    modifier = Modifier.size(20.dp)
                )
            }
        }
    }
}
