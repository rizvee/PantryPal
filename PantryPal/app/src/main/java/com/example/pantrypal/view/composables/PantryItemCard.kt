package com.example.pantrypal.view.composables

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.pantrypal.model.PantryItem
import com.example.pantrypal.view.ui.theme.PantryPalTheme // Import your app's theme
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.concurrent.TimeUnit

@Composable
fun PantryItemCard(pantryItem: PantryItem, modifier: Modifier = Modifier) {
    Card(
        modifier = modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = pantryItem.name,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Qty: ${pantryItem.quantity}",
                    style = MaterialTheme.typography.bodyMedium
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Purchased: ${formatDate(pantryItem.purchaseDate)}",
                    style = MaterialTheme.typography.bodySmall
                )
                Text(
                    text = "Expires: ${formatDate(pantryItem.predictedExpiryDate)}",
                    style = MaterialTheme.typography.bodySmall,
                    color = getExpiryColor(pantryItem.predictedExpiryDate).copy(alpha = 0.8f)
                )
            }
            Spacer(modifier = Modifier.width(16.dp))
            FreshnessIndicator(expiryDate = pantryItem.predictedExpiryDate)
        }
    }
}

@Composable
fun FreshnessIndicator(expiryDate: Long) {
    val daysUntilExpiry = TimeUnit.MILLISECONDS.toDays(expiryDate - System.currentTimeMillis())
    val indicatorColor = getExpiryColor(expiryDate)

    Box(
        modifier = Modifier
            .size(24.dp)
            .clip(CircleShape)
            .background(indicatorColor)
    ) {
        // Optionally, you can put text inside the circle, e.g., days left
        // Text(text = daysUntilExpiry.toString(), modifier = Modifier.align(Alignment.Center), color = Color.White)
    }
}

fun getExpiryColor(expiryDate: Long): Color {
    val daysUntilExpiry = TimeUnit.MILLISECONDS.toDays(expiryDate - System.currentTimeMillis())

    return when {
        daysUntilExpiry < 0 -> Color.Red // Expired
        daysUntilExpiry <= 3 -> Color(0xFFFFA500) // Orange - Nearing expiry (3 days or less)
        daysUntilExpiry <= 7 -> Color.Yellow // About to expire (7 days or less)
        else -> Color.Green // Fresh
    }
}

fun formatDate(timestamp: Long): String {
    return try {
        val sdf = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault())
        val netDate = Date(timestamp)
        sdf.format(netDate)
    } catch (e: Exception) {
        "N/A"
    }
}

@Preview(showBackground = true)
@Composable
fun PantryItemCardPreview_Fresh() {
    PantryPalTheme {
        PantryItemCard(
            PantryItem(
                id = 1,
                name = "Fresh Apples",
                quantity = "1 kg",
                purchaseDate = System.currentTimeMillis() - TimeUnit.DAYS.toMillis(2), // Purchased 2 days ago
                predictedExpiryDate = System.currentTimeMillis() + TimeUnit.DAYS.toMillis(10) // Expires in 10 days
            )
        )
    }
}

@Preview(showBackground = true)
@Composable
fun PantryItemCardPreview_NearingExpiry() {
    PantryPalTheme {
        PantryItemCard(
            PantryItem(
                id = 2,
                name = "Milk",
                quantity = "1 Carton",
                purchaseDate = System.currentTimeMillis() - TimeUnit.DAYS.toMillis(1), // Purchased 1 day ago
                predictedExpiryDate = System.currentTimeMillis() + TimeUnit.DAYS.toMillis(2) // Expires in 2 days
            )
        )
    }
}

@Preview(showBackground = true)
@Composable
fun PantryItemCardPreview_Expired() {
    PantryPalTheme {
        PantryItemCard(
            PantryItem(
                id = 3,
                name = "Old Bread",
                quantity = "1 Loaf",
                purchaseDate = System.currentTimeMillis() - TimeUnit.DAYS.toMillis(10), // Purchased 10 days ago
                predictedExpiryDate = System.currentTimeMillis() - TimeUnit.DAYS.toMillis(1) // Expired 1 day ago
            )
        )
    }
}

@Preview(showBackground = true)
@Composable
fun PantryItemCardPreview_AboutToExpire() {
    PantryPalTheme {
        PantryItemCard(
            PantryItem(
                id = 4,
                name = "Chicken Breast",
                quantity = "500g",
                purchaseDate = System.currentTimeMillis() - TimeUnit.DAYS.toMillis(3),
                predictedExpiryDate = System.currentTimeMillis() + TimeUnit.DAYS.toMillis(6) // Expires in 6 days
            )
        )
    }
}
