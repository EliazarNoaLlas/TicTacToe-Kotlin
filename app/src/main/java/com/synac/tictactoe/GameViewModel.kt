package com.synac.tictactoe

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel

// Clase GameViewModel que hereda de ViewModel para manejar el estado y la lógica del juego
class GameViewModel : ViewModel() {

    // Estado del juego (state) almacenado como una propiedad observable para que Compose reaccione a sus cambios.
    var state by mutableStateOf(GameState())

    // Mapa mutable para almacenar el valor de cada celda del tablero (1 a 9) con valores iniciales como NONE (vacío)
    val boardItems: MutableMap<Int, BoardCellValue> = mutableMapOf(
        1 to BoardCellValue.NONE,
        2 to BoardCellValue.NONE,
        3 to BoardCellValue.NONE,
        4 to BoardCellValue.NONE,
        5 to BoardCellValue.NONE,
        6 to BoardCellValue.NONE,
        7 to BoardCellValue.NONE,
        8 to BoardCellValue.NONE,
        9 to BoardCellValue.NONE,
    )

    // Función que gestiona las acciones de los usuarios, como tocar el tablero o pulsar "Jugar de nuevo"
    fun onAction(action: UserAction) {
        when (action) {
            // Si se toca una celda, se llama a la función addValueToBoard para actualizar el tablero
            is UserAction.BoardTapped -> {
                addValueToBoard(action.cellNo)
            }
            // Si se pulsa el botón de "Jugar de nuevo", se reinicia el juego
            UserAction.PlayAgainButtonClicked -> {
                gameReset()
            }
        }
    }

    // Función que reinicia el juego y restablece el tablero y el estado del juego
    private fun gameReset() {
        // Itera sobre cada celda y las establece en NONE (vacío)
        boardItems.forEach { (i, _) ->
            boardItems[i] = BoardCellValue.NONE
        }
        // Reinicia el estado del juego al inicio: turno del jugador 'O', sin victoria, sin ganadores
        state = state.copy(
            hintText = "Player 'O' turn",
            currentTurn = BoardCellValue.CIRCLE,
            victoryType = VictoryType.NONE,
            hasWon = false
        )
    }

    // Función que agrega el valor correspondiente ('O' o 'X') a la celda tocada
    private fun addValueToBoard(cellNo: Int) {
        // Si la celda ya tiene un valor distinto de NONE, se ignora la acción
        if (boardItems[cellNo] != BoardCellValue.NONE) {
            return
        }
        // Si es el turno del jugador 'O', se coloca un 'O' en la celda seleccionada
        if (state.currentTurn == BoardCellValue.CIRCLE) {
            boardItems[cellNo] = BoardCellValue.CIRCLE
            // Verifica si el jugador 'O' ha ganado
            if (checkForVictory(BoardCellValue.CIRCLE)) {
                // Si ha ganado, actualiza el estado con el mensaje de victoria y aumenta su contador de victorias
                state = state.copy(
                    hintText = "Player 'O' Won",
                    playerCircleCount = state.playerCircleCount + 1,
                    currentTurn = BoardCellValue.NONE, // Detiene el turno porque el juego ha terminado
                    hasWon = true
                )
                // Verifica si el tablero está lleno y no hay ganador, lo que implica un empate
            } else if (hasBoardFull()) {
                state = state.copy(
                    hintText = "Game Draw", // Muestra el mensaje de empate
                    drawCount = state.drawCount + 1
                )
            } else {
                // Si no hay ganador ni empate, pasa el turno al jugador 'X'
                state = state.copy(
                    hintText = "Player 'X' turn",
                    currentTurn = BoardCellValue.CROSS
                )
            }
            // Si es el turno del jugador 'X', se coloca una 'X' en la celda seleccionada
        } else if (state.currentTurn == BoardCellValue.CROSS) {
            boardItems[cellNo] = BoardCellValue.CROSS
            // Verifica si el jugador 'X' ha ganado
            if (checkForVictory(BoardCellValue.CROSS)) {
                state = state.copy(
                    hintText = "Player 'X' Won",
                    playerCrossCount = state.playerCrossCount + 1,
                    currentTurn = BoardCellValue.NONE, // Detiene el turno porque el juego ha terminado
                    hasWon = true
                )
                // Verifica si hay empate
            } else if (hasBoardFull()) {
                state = state.copy(
                    hintText = "Game Draw",
                    drawCount = state.drawCount + 1
                )
            } else {
                // Si no hay ganador ni empate, vuelve al turno del jugador 'O'
                state = state.copy(
                    hintText = "Player 'O' turn",
                    currentTurn = BoardCellValue.CIRCLE
                )
            }
        }
    }

    // Función que comprueba si el jugador actual ha ganado, verificando todas las posibles combinaciones de victoria
    private fun checkForVictory(boardValue: BoardCellValue): Boolean {
        when {
            // Verifica si hay tres en línea en la primera fila horizontal
            boardItems[1] == boardValue && boardItems[2] == boardValue && boardItems[3] == boardValue -> {
                state = state.copy(victoryType = VictoryType.HORIZONTAL1)
                return true
            }
            // Verifica si hay tres en línea en la segunda fila horizontal
            boardItems[4] == boardValue && boardItems[5] == boardValue && boardItems[6] == boardValue -> {
                state = state.copy(victoryType = VictoryType.HORIZONTAL2)
                return true
            }
            // Verifica si hay tres en línea en la tercera fila horizontal
            boardItems[7] == boardValue && boardItems[8] == boardValue && boardItems[9] == boardValue -> {
                state = state.copy(victoryType = VictoryType.HORIZONTAL3)
                return true
            }
            // Verifica si hay tres en línea en la primera columna vertical
            boardItems[1] == boardValue && boardItems[4] == boardValue && boardItems[7] == boardValue -> {
                state = state.copy(victoryType = VictoryType.VERTICAL1)
                return true
            }
            // Verifica si hay tres en línea en la segunda columna vertical
            boardItems[2] == boardValue && boardItems[5] == boardValue && boardItems[8] == boardValue -> {
                state = state.copy(victoryType = VictoryType.VERTICAL2)
                return true
            }
            // Verifica si hay tres en línea en la tercera columna vertical
            boardItems[3] == boardValue && boardItems[6] == boardValue && boardItems[9] == boardValue -> {
                state = state.copy(victoryType = VictoryType.VERTICAL3)
                return true
            }
            // Verifica si hay tres en línea en la diagonal principal (1-5-9)
            boardItems[1] == boardValue && boardItems[5] == boardValue && boardItems[9] == boardValue -> {
                state = state.copy(victoryType = VictoryType.DIAGONAL1)
                return true
            }
            // Verifica si hay tres en línea en la diagonal inversa (3-5-7)
            boardItems[3] == boardValue && boardItems[5] == boardValue && boardItems[7] == boardValue -> {
                state = state.copy(victoryType = VictoryType.DIAGONAL2)
                return true
            }
            else -> return false // Si no hay ninguna combinación ganadora, retorna false
        }
    }

    // Función que comprueba si el tablero está lleno (sin celdas vacías)
    private fun hasBoardFull(): Boolean {
        // Si alguna celda aún tiene el valor NONE, el tablero no está lleno
        if (boardItems.containsValue(BoardCellValue.NONE)) return false
        return true // Si no hay celdas vacías, el tablero está lleno
    }
}