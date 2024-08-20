import android.graphics.Bitmap
import android.util.Log
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.ai.client.generativeai.GenerativeModel
import com.google.ai.client.generativeai.type.content
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class MainViewModel : ViewModel() {

    // State management
    private val _apiState = MutableStateFlow(GeminiState())
    val apiState: StateFlow<GeminiState> = _apiState.asStateFlow()

    // Your generative AI model
    private val generativeAiModel: GenerativeModel = GenerativeModel(
        modelName = "gemini-1.5-flash-001", apiKey = "AIzaSyDCsYUana8weqspfXrn9_jhwpiddNMc6Xw"
    )

    // Function to fetch image analysis
    fun getImage(bitImage: Bitmap) {
        _apiState.update { it.copy(isLoading = true) }
        viewModelScope.launch {
            delay(1000)
            try {
                val response = generativeAiModel.generateContent(
                    content {
                        image(bitImage)
                        text(
                            "Identify the type of disease in the given image.\n" +
                                    "Give the output in the following format:-\n" +
                                    "Disease Name: Clearly state the name of the disease.\n" +
                                    "Causes: Mention the cause(s) of the disease, including the scientific name if applicable and ensure the text is concise.\n" +
                                    "Symptoms: List the key symptoms associated with the disease. Use bullet points for clarity.\n" +
                                    "Preventions: Provide a list of preventive measures to avoid or mitigate the disease. Use bullet points and ensure the text is concise.\n" +
                                    "Suggested Pesticides: List around 3 pesticides that can be used to treat the disease. Use bullet points for clarity.\n" +
                                    "Suggested Organic Solutions: List 3 organic solutions that can be used to treat the disease. Use bullet points for clarity."
                        )
                    }
                )
                Log.e("ViewModel",response.text.toString())
                val result = Response(response.text.toString(), bitImage)
                _apiState.update { it. copy(response = result, isLoading = false)}
            } catch (e: Exception) {
                _apiState.update { it.copy(isLoading = false, errorMessage = e.message) }
            }
        }
    }

    // Function to format the text for the UI
    fun formattedText(text: String) : String {
        val annotatedString = buildAnnotatedString {
            var startIndex = 0
            var endIndex: Int
            while (startIndex < text.length) {
                val startBold = text.indexOf("**", startIndex)
                if (startBold == -1) {
                    // Add the remaining text as normal
                    append(text.substring(startIndex))
                    break
                } else {
                    // Add the text before the bold section
                    append(text.substring(startIndex, startBold))
                    endIndex = text.indexOf("**", startBold + 2)
                    if (endIndex == -1) {
                        // If there's no matching closing asterisks, just add the rest of the text
                        append(text.substring(startBold))
                        break
                    } else {
                        // Add the bold text
                        withStyle(style = SpanStyle(fontWeight = FontWeight.Bold)) {
                            append(text.substring(startBold + 2, endIndex))
                        }
                        startIndex = endIndex + 2
                    }
                }
            }
        }
        return annotatedString.toString()
    }

}

data class GeminiState(
    val isLoading: Boolean = false,
    val response: Response? = null,
    val errorMessage: String? = null
)

//sealed class GeminiState {
//    object Loading : GeminiState()
//    data class Success(val response: Response) : GeminiState()
//    data class Error(val errorMessage: String) : GeminiState()
//}

// Response data class
data class Response(
    val text: String,
    val bitImg: Bitmap
)
