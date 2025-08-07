package com.judahben149.tala.presentation.screen

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.judahben149.tala.presentation.navigation.PrefsTestComponent

@Composable
fun PrefsTestScreen(
    component: PrefsTestComponent,
    modifier: Modifier = Modifier
) {
    val state by component.state.collectAsState()
    var stringKey by remember { mutableStateOf("") }
    var stringValue by remember { mutableStateOf("") }
    var intKey by remember { mutableStateOf("") }
    var intValue by remember { mutableStateOf("") }
    
    Column(
        modifier = modifier.fillMaxSize()
    ) {
        // Top bar
        TopAppBar(
            title = { Text("Preferences Test") },
            navigationIcon = {
                IconButton(onClick = { component.onBackPressed() }) {
                    Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                }
            }
        )
        
        LazyColumn(
            modifier = Modifier.weight(1f).padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // String input
            item {
                Card {
                    Column(
                        modifier = Modifier.padding(16.dp)
                    ) {
                        Text(
                            text = "Save String",
                            style = MaterialTheme.typography.titleMedium
                        )
                        
                        Spacer(modifier = Modifier.height(16.dp))
                        
                        OutlinedTextField(
                            value = stringKey,
                            onValueChange = { stringKey = it },
                            label = { Text("Key") },
                            modifier = Modifier.fillMaxWidth()
                        )
                        
                        Spacer(modifier = Modifier.height(8.dp))
                        
                        OutlinedTextField(
                            value = stringValue,
                            onValueChange = { stringValue = it },
                            label = { Text("Value") },
                            modifier = Modifier.fillMaxWidth()
                        )
                        
                        Spacer(modifier = Modifier.height(16.dp))
                        
                        Button(
                            onClick = {
                                if (stringKey.isNotBlank() && stringValue.isNotBlank()) {
                                    component.onSaveString(stringKey, stringValue)
                                    stringKey = ""
                                    stringValue = ""
                                }
                            },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text("Save String")
                        }
                    }
                }
            }
            
            // Int input
            item {
                Card {
                    Column(
                        modifier = Modifier.padding(16.dp)
                    ) {
                        Text(
                            text = "Save Integer",
                            style = MaterialTheme.typography.titleMedium
                        )
                        
                        Spacer(modifier = Modifier.height(16.dp))
                        
                        OutlinedTextField(
                            value = intKey,
                            onValueChange = { intKey = it },
                            label = { Text("Key") },
                            modifier = Modifier.fillMaxWidth()
                        )
                        
                        Spacer(modifier = Modifier.height(8.dp))
                        
                        OutlinedTextField(
                            value = intValue,
                            onValueChange = { intValue = it },
                            label = { Text("Value") },
                            modifier = Modifier.fillMaxWidth()
                        )
                        
                        Spacer(modifier = Modifier.height(16.dp))
                        
                        Button(
                            onClick = {
                                if (intKey.isNotBlank() && intValue.isNotBlank()) {
                                    val value = intValue.toIntOrNull()
                                    if (value != null) {
                                        component.onSaveInt(intKey, value)
                                        intKey = ""
                                        intValue = ""
                                    }
                                }
                            },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text("Save Integer")
                        }
                    }
                }
            }
            
            // Saved strings
            item {
                Text(
                    text = "Saved Strings",
                    style = MaterialTheme.typography.titleMedium
                )
            }
            
            items(state.savedStrings.toList()) { (key, value) ->
                Card {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(
                            modifier = Modifier.weight(1f)
                        ) {
                            Text(
                                text = key,
                                style = MaterialTheme.typography.titleSmall
                            )
                            Text(
                                text = value,
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }
                    }
                }
            }
            
            // Saved integers
            item {
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = "Saved Integers",
                    style = MaterialTheme.typography.titleMedium
                )
            }
            
            items(state.savedInts.toList()) { (key, value) ->
                Card {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(
                            modifier = Modifier.weight(1f)
                        ) {
                            Text(
                                text = key,
                                style = MaterialTheme.typography.titleSmall
                            )
                            Text(
                                text = value.toString(),
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }
                    }
                }
            }
        }
    }
}
