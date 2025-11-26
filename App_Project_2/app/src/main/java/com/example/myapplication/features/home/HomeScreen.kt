package com.example.myapplication.features.home

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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.animation.Crossfade
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay
import com.example.myapplication.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    onLogin: () -> Unit,
    onSignUp: () -> Unit,
    onRegister: () -> Unit,
    modifier: Modifier = Modifier
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { 
                    Image(
                        painter = painterResource(id = R.drawable.younext),
                        contentDescription = "youNext Logo",
                        modifier = Modifier
                            .height(70.dp)
                            .width(130.dp)
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xFF001F54),
                    titleContentColor = MaterialTheme.colorScheme.onPrimary,
                    navigationIconContentColor = MaterialTheme.colorScheme.onPrimary
                )
            )
        }
    ) { innerPadding ->
            LazyColumn(
                modifier = modifier
                    .fillMaxSize()
                    .padding(innerPadding)
            ) {
                item { HeroSection(onLogin = onLogin, onSignUp = onSignUp) }
                item { StatsCarousel() }
                item { TestimonialsCarousel() }
                item { RegisterCTA(onRegister) }
                item { FooterLinks() }
                item { Spacer(modifier = Modifier.height(24.dp)) }
            }
        }
    }

@Composable
private fun HeroSection(onLogin: () -> Unit, onSignUp: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.surfaceVariant)
            .padding(20.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Your Gateway to Professional Growth",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.ExtraBold,
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "Plan your next steps with career paths, learning roadmaps, and opportunities.",
            style = MaterialTheme.typography.bodyMedium,
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(12.dp))
        Image(
            painter = painterResource(id = R.drawable.banner),
            contentDescription = "YouNext banner",
            modifier = Modifier
                .fillMaxWidth()
                .height(160.dp)
        )
        Spacer(modifier = Modifier.height(16.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            Button(onClick = onLogin) { Text("LOGIN") }
            Button(onClick = onSignUp) { Text("SIGN UP") }
        }
    }
}

@Composable
private fun StatsCarousel() {
    val cardWhite = Color.White
    val textDark = Color(0xFF1C1C1C)
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 12.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        StatCard(
            title = "5000+",
            subtitle = "Opportunities",
            modifier = Modifier.weight(1f),
            containerColor = cardWhite,
            contentColor = textDark
        )
        StatCard(
            title = "1000+",
            subtitle = "Success Stories",
            modifier = Modifier.weight(1f),
            containerColor = cardWhite,
            contentColor = textDark
        )
        StatCard(
            title = "200+",
            subtitle = "Partner Companies",
            modifier = Modifier.weight(1f),
            containerColor = cardWhite,
            contentColor = textDark
        )
    }
}

@Composable
private fun StatCard(
    title: String,
    subtitle: String,
    modifier: Modifier = Modifier,
    containerColor: Color = MaterialTheme.colorScheme.primaryContainer,
    contentColor: Color = MaterialTheme.colorScheme.onPrimaryContainer
) {
    ElevatedCard(
        modifier = modifier,
        colors = CardDefaults.elevatedCardColors(containerColor = containerColor),
        elevation = CardDefaults.elevatedCardElevation(defaultElevation = 4.dp),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(
            modifier = Modifier.padding(vertical = 16.dp, horizontal = 8.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(text = title, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold, color = contentColor, textAlign = TextAlign.Center)
            Text(text = subtitle, style = MaterialTheme.typography.bodyMedium, color = contentColor, textAlign = TextAlign.Center)
        }
    }
}

@Composable
private fun TestimonialsCarousel() {
    val testimonials = listOf(
        "Shruti Khisa." to "Found my dream job!",
        "Farhan Noor" to "Easy to navigate!",
        "Shaira Diba" to "Great recommendations!"
    )
    // Map testimonial order to drawable names you provided
    val avatarNames = listOf("shruti", "noor", "diba")
    var index by remember { mutableIntStateOf(0) }
    // Auto-advance
    val total = testimonials.size
    androidx.compose.runtime.LaunchedEffect(total) {
        while (true) {
            delay(3000)
            index = (index + 1) % total
        }
    }
    Column(modifier = Modifier.padding(16.dp)) {
        Text(text = "People's Remarks", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
        Spacer(modifier = Modifier.height(8.dp))
        Row(
            modifier = Modifier
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = { index = if (index - 1 < 0) total - 1 else index - 1 }) {
                Icon(Icons.Default.ArrowBack, contentDescription = "Previous")
            }
            Box(
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = 8.dp)
                    .height(140.dp)
            ) {
                Crossfade(targetState = index, label = "testimonialCrossfade") { i ->
                    val (name, remark) = testimonials[i]
                    // Optional avatar images: add review1.jpg, review2.jpg, review3.jpg to res/drawable
                    val context = androidx.compose.ui.platform.LocalContext.current
                    val resName = avatarNames.getOrNull(i) ?: ""
                    val resId = context.resources.getIdentifier(resName, "drawable", context.packageName)
                    val avatar = if (resId != 0) resId else null
                    TestimonialCard(name = name, remark = remark, imageResId = avatar, containerColor = Color.White, contentColor = Color(0xFF1C1C1C))
                }
            }
            IconButton(onClick = { index = (index + 1) % total }) {
                Icon(Icons.Default.ArrowForward, contentDescription = "Next")
            }
        }
    }
}

@Composable
private fun TestimonialCard(
    name: String,
    remark: String,
    modifier: Modifier = Modifier,
    imageResId: Int? = null,
    containerColor: Color = MaterialTheme.colorScheme.secondaryContainer,
    contentColor: Color = MaterialTheme.colorScheme.onSecondaryContainer
) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(containerColor = containerColor),
        shape = RoundedCornerShape(14.dp)
    ) {
        Row(modifier = Modifier.padding(18.dp), verticalAlignment = Alignment.CenterVertically) {
            if (imageResId != null) {
                Image(painter = painterResource(id = imageResId), contentDescription = name, modifier = Modifier.size(56.dp))
                Spacer(modifier = Modifier.width(16.dp))
            }
            Column(modifier = Modifier.weight(1f)) {
                Text(text = name, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = contentColor)
                Spacer(modifier = Modifier.height(6.dp))
                Text(text = remark, style = MaterialTheme.typography.bodyLarge, color = contentColor)
            }
        }
    }
}

@Composable
private fun RegisterCTA(onRegister: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Are you ready to take your next step?",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Medium,
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(8.dp))
        Button(onClick = onRegister) { Text("REGISTER NOW") }
    }
}

@Composable
private fun FooterLinks() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 24.dp),
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        Text(text = "About Us", style = MaterialTheme.typography.bodySmall)
        Text(text = "Contact", style = MaterialTheme.typography.bodySmall)
        Text(text = "Privacy Policy", style = MaterialTheme.typography.bodySmall)
    }
}


