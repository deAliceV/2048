package com.example.a2048_activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MotionEvent
import android.view.View
import android.widget.Button
import androidx.appcompat.app.AlertDialog
import kotlin.math.abs
import kotlin.random.Random


private var initialX: Float = 0.toFloat()
private var initialY: Float = 0.toFloat()
private lateinit var matriz: Array<IntArray>
private val scope = Score()
lateinit var scoreButton: Button

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {


        matriz = Array(4) { IntArray(4) }
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_main)
        asignarNumerosAleatorios()

        val layout = findViewById<View>(R.id.root_layout)
        scoreButton = findViewById(R.id.score)
        layout.setOnTouchListener { _, event ->
            handleTouch(event)
            true
        }
    }


    private fun asignarNumerosAleatorios() {
        // Genera dos posiciones aleatorias en la matriz
        val randomRow1 = (0 until 4).random()
        val randomCol1 = (0 until 4).random()
        var randomRow2: Int
        var randomCol2: Int

        do {
            randomRow2 = (0 until 4).random()
            randomCol2 = (0 until 4).random()
        } while (randomRow1 == randomRow2 && randomCol1 == randomCol2)

        // Asigna un número aleatorio (2 o 4) en cada posición generada
        val randomNumber1 = (0..1).random() * 2 + 2
        val randomNumber2 = (0..1).random() * 2 + 2

        matriz[randomRow1][randomCol1] = randomNumber1
        matriz[randomRow2][randomCol2] = randomNumber2

        // Actualiza los textos de los botones con los números generados
        val button1 = findViewById<Button>(resources.getIdentifier("button${randomRow1 * 4 + randomCol1 + 1}", "id", packageName))
        val button2 = findViewById<Button>(resources.getIdentifier("button${randomRow2 * 4 + randomCol2 + 1}", "id", packageName))
        button1.text = randomNumber1.toString()
        button2.text = randomNumber2.toString()
    }
    private fun asignarNumeroAleatorioDespuesDeMovimiento() {
        // Encuentra una posición vacía en la matriz
        var emptyRow = -1
        var emptyCol = -1

        for (i in 0 until 4) {
            for (j in 0 until 4) {
                if (matriz[i][j] == 0) {
                    emptyRow = i
                    emptyCol = j
                    break
                }
            }
            if (emptyRow != -1 && emptyCol != -1) {
                break
            }
        }

        // Si hay una posición vacía, asigna un número aleatorio en esa posición
        if (emptyRow != -1 && emptyCol != -1) {
            val randomNumber = (0..1).random() * 2 + 2
            matriz[emptyRow][emptyCol] = randomNumber

            // Actualiza el texto del botón correspondiente con el número generado
            val button = findViewById<Button>(resources.getIdentifier("button${emptyRow * 4 + emptyCol + 1}", "id", packageName))
            button.text = randomNumber.toString()
        }
    }


    private fun handleTouch(event: MotionEvent) {
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                initialX = event.x
                initialY = event.y
            }

            MotionEvent.ACTION_UP -> {
                val finalX = event.x
                val finalY = event.y

                val deltaX = finalX - initialX
                val deltaY = finalY - initialY

                if (abs(deltaX) > abs(deltaY)) {
                    if (deltaX > 0) {
                        moverNumeros("derecha")
                    } else {
                        moverNumeros("izquierda")
                    }
                } else {
                    if (deltaY > 0) {
                        moverNumeros("abajo")
                    } else {
                        moverNumeros("arriba")
                    }
                }
            }
        }

    }
    private fun moverNumeros(direccion: String) {
        // Implementa la lógica para mover los números en la matriz en la dirección especificada
        // Luego actualiza los botones
        when (direccion) {
            "izquierda" -> moverNumerosIzquierda()
            "derecha" -> moverNumerosDerecha()
            "arriba" -> moverNumerosArriba()
            "abajo" -> moverNumerosAbajo()
        }
        if (isGameOver()) {
            showGameOverDialog()
            return // Detener la ejecución adicional si el juego ha terminado
        }
        asignarNumeroAleatorioDespuesDeMovimiento()
        actualizarBotones()

        scoreButton.text = scope.getScore().toString()

    }
    private fun actualizarBotones() {
        // Actualiza los textos de los botones según la matriz
        for (i in 0 until 4) {
            for (j in 0 until 4) {
                val button = findViewById<Button>(resources.getIdentifier("button${i * 4 + j + 1}", "id", packageName))
                val number = matriz[i][j]
                button.text = if (number != 0) number.toString() else ""
            }
        }
    }
    private fun moverNumerosIzquierda() {
        // Implementa la lógica para mover los números hacia la izquierda
        for (i in 0 until 4) {
            val fila = matriz[i].toMutableList()
            fila.removeAll { it == 0 }
            fila.addAll(List(4 - fila.size) { 0 })
            for (j in 0 until 3) {
                if (fila[j] == fila[j + 1]) {
                    fila[j] *= 2
                    fila[j + 1] = 0
                    scope.updateScore(fila[j])

                }
            }
            fila.removeAll { it == 0 }
            fila.addAll(List(4 - fila.size) { 0 })
            matriz[i] = fila.toIntArray()
        }

    }

    private fun moverNumerosDerecha() {
        // Implementa la lógica para mover los números hacia la derecha
        for (i in 0 until 4) {
            val fila = matriz[i].toMutableList()
            fila.removeAll { it == 0 }
            fila.addAll(0, List(4 - fila.size) { 0 })
            for (j in 3 downTo 1) {
                if (fila[j] == fila[j - 1]) {
                    fila[j] *= 2
                    fila[j - 1] = 0
                    scope.updateScore(fila[j])

                }
            }
            fila.removeAll { it == 0 }
            fila.addAll(0, List(4 - fila.size) { 0 })
            matriz[i] = fila.toIntArray()
        }

    }

    private fun moverNumerosArriba() {
        // Implementa la lógica para mover los números hacia arriba
        for (j in 0 until 4) {
            val columna = mutableListOf<Int>()
            for (i in 0 until 4) {
                columna.add(matriz[i][j])
            }
            columna.removeAll { it == 0 }
            columna.addAll(List(4 - columna.size) { 0 })
            for (i in 0 until 3) {
                if (columna[i] == columna[i + 1]) {
                    columna[i] *= 2
                    columna[i + 1] = 0
                    scope.updateScore(columna[i])

                }
            }
            columna.removeAll { it == 0 }
            columna.addAll(List(4 - columna.size) { 0 })
            for (i in 0 until 4) {
                matriz[i][j] = columna[i]
            }
        }

    }

    private fun moverNumerosAbajo() {
        // Implementa la lógica para mover los números hacia abajo
        for (j in 0 until 4) {
            val columna = mutableListOf<Int>()
            for (i in 0 until 4) {
                columna.add(matriz[i][j])
            }
            columna.removeAll { it == 0 }
            columna.addAll(0, List(4 - columna.size) { 0 })
            for (i in 3 downTo 1) {
                if (columna[i] == columna[i - 1]) {
                    columna[i] *= 2
                    columna[i - 1] = 0
                    scope.updateScore(columna[i])
                }
            }
            columna.removeAll { it == 0 }
            columna.addAll(0, List(4 - columna.size) { 0 })
            for (i in 0 until 4) {
                matriz[i][j] = columna[i]
            }
        }

    }
    private fun isGameOver(): Boolean {
        // Verificar si hay movimientos posibles en la matriz
        for (i in 0 until 4) {
            for (j in 0 until 4) {
                // Verificar si hay una casilla vacía
                if (matriz[i][j] == 0) {
                    return false
                }
                // Verificar si hay números adyacentes que puedan combinarse
                if ((i < 3 && matriz[i][j] == matriz[i + 1][j]) || (j < 3 && matriz[i][j] == matriz[i][j + 1])) {
                    return false
                }
            }
        }
        return true // No se encontraron movimientos posibles, el juego ha terminado
    }

    private fun showGameOverDialog() {
        // Mostrar un diálogo o una notificación para informar al jugador que el juego ha terminado
        // Por ejemplo, un AlertDialog
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Game Over")
        builder.setMessage("No hay movimientos posibles. ¡Has perdido!")
        builder.setPositiveButton("OK") { _, _ ->
            // Puedes reiniciar el juego aquí si lo deseas
        }
        builder.setCancelable(false)
        builder.show()
    }


}