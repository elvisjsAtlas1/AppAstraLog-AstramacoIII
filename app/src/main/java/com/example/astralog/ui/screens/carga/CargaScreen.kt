package com.example.astralog.ui.screens.carga

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel

@Composable
fun CargaScreen(
    onBack: () -> Unit,
    viewModel: CargaViewModel = viewModel()
) {
    val state by viewModel.uiState.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.cargarDatos()
    }

    when {
        state.isLoading -> {
            Column(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.Center
            ) {
                CircularProgressIndicator(modifier = Modifier.padding(24.dp))
            }
        }

        else -> {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(
                    text = "Mi carga",
                    style = MaterialTheme.typography.headlineMedium
                )

                state.transportista?.let { t ->
                    Card(modifier = Modifier.fillMaxWidth()) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text("Transportista: ${t.nombre} ${t.apellidos}")
                            Text("Tipo: ${t.tipoTransporte}")
                            Text("Placa: ${t.placa}")
                        }
                    }
                }

                state.error?.let {
                    Text(
                        text = it,
                        color = MaterialTheme.colorScheme.error
                    )
                }

                state.success?.let {
                    Text(
                        text = it,
                        color = MaterialTheme.colorScheme.primary
                    )
                }

                Card(modifier = Modifier.fillMaxWidth()) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = "Carga actual",
                            style = MaterialTheme.typography.titleMedium
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        if (state.carga != null) {
                            Text("Material: ${state.carga!!.tipoMaterial}")
                            Text("Cantidad disponible: ${state.carga!!.cantidadDisponible}")
                        } else {
                            Text("Aún no tienes carga registrada")
                        }
                    }
                }

                Card(modifier = Modifier.fillMaxWidth()) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = "Subir / reemplazar carga actual",
                            style = MaterialTheme.typography.titleMedium
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        Text("Selecciona el material")

                        Row {
                            RadioButton(
                                selected = state.tipoMaterialSeleccionado == "PANDERETA",
                                onClick = { viewModel.onTipoMaterialChange("PANDERETA") }
                            )
                            Text(
                                text = "PANDERETA",
                                modifier = Modifier.padding(top = 12.dp)
                            )

                            Spacer(modifier = Modifier.width(12.dp))

                            RadioButton(
                                selected = state.tipoMaterialSeleccionado == "TECHO",
                                onClick = { viewModel.onTipoMaterialChange("TECHO") }
                            )
                            Text(
                                text = "TECHO",
                                modifier = Modifier.padding(top = 12.dp)
                            )
                        }

                        OutlinedTextField(
                            value = state.cantidadTexto,
                            onValueChange = viewModel::onCantidadChange,
                            label = { Text("Cantidad disponible") },
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 8.dp),
                            singleLine = true
                        )

                        Button(
                            onClick = { viewModel.subirCargaActual() },
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 12.dp)
                        ) {
                            Text("Guardar carga actual")
                        }
                    }
                }

                Card(modifier = Modifier.fillMaxWidth()) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = "Aumentar carga actual",
                            style = MaterialTheme.typography.titleMedium
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        Text("Solo funciona si el material coincide con tu carga actual")

                        OutlinedTextField(
                            value = state.cantidadAumentarTexto,
                            onValueChange = viewModel::onCantidadAumentarChange,
                            label = { Text("Cantidad a agregar") },
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 8.dp),
                            singleLine = true
                        )

                        Button(
                            onClick = { viewModel.aumentarCargaActual() },
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 12.dp)
                        ) {
                            Text("Aumentar carga")
                        }
                    }
                }

                TextButton(
                    onClick = onBack,
                    contentPadding = PaddingValues(0.dp)
                ) {
                    Text("Volver al perfil")
                }
            }
        }
    }
}