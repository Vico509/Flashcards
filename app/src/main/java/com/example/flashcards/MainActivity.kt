package com.example.flashcards

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.result.contract.ActivityResultContracts
import com.google.android.material.snackbar.Snackbar

class MainActivity : AppCompatActivity() {
    //private var isShowingAnswers = true
    //private var lauchNewCardActivity = true
    private lateinit var flashcardQuestion : TextView
    private lateinit var flashcardAnswer : TextView
    private lateinit var addButton : ImageView
    private lateinit var flashcardDatabase: FlashcardDatabase
    private  lateinit var  nextButton : ImageView
    private var allFlashcards = mutableListOf<Flashcard>()
    private var currentCardDisplayIndex = 0
    private lateinit var deleteButton : ImageView
    private lateinit var editButton   : ImageView
    private var cardToEdit: Flashcard? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        // The Answer textView and the eyes icons layout
       // val flashcardQuestion = findViewById<TextView>(R.id.flashcard_question)
        //val flashcardAnswer = findViewById<TextView>(R.id.flashcard_answer)
        //val flashcardAnswer1 = findViewById<TextView>(R.id.flashcard_answer1)
       // val flashcardAnswer2 = findViewById<TextView>(R.id.flashcard_answer2)
        //val ivToggleChoiceVisibility = findViewById<ImageView>(R.id.add_button_image)

        // Look up the views in layout
        initializeViews()

        // Initialization of flashcardDatabase variable
        flashcardDatabase = FlashcardDatabase(this)
        allFlashcards = flashcardDatabase.getAllCards().toMutableList()

        nextButton.setOnClickListener {
            // First check whether you have any maps to display
            if (allFlashcards.isEmpty()) {
                // If you have no cards, display a message or perform another appropriate action.
                Snackbar.make(
                    flashcardQuestion,
                    "No cards available, add some first.",
                    Snackbar.LENGTH_SHORT
                ).show()
                return@setOnClickListener
            }

            flashcardAnswer.visibility = View.INVISIBLE
            flashcardQuestion.visibility = View.VISIBLE

            // Get a random index
            val randomIndex = getRandomNumber(0,allFlashcards.size -1)

            // Obtain the map to be displayed using the random index
            val (question,answer) = allFlashcards[randomIndex]

            // Display the question and answer
            flashcardAnswer.text = answer
            flashcardQuestion.text = question

//            // Reset to initial state
//            toggleAnswerVisibility()
        }



        // if condition for verify if the list of card is empty or not, if the list of card not empty you can display the memory save.
        if (allFlashcards.size > 0){
            flashcardQuestion.text = allFlashcards[0].question
            flashcardAnswer.text = allFlashcards[0].answer
        }

        flashcardQuestion.setOnClickListener {
            toggleAnswerVisibility()
        }


//
//


//
//        fun launchComposeView() {
//            val i = Intent(this,AddCardActivity::class.java)
//            resultLauncher.launch(i)
//        }
//        //Add OnClickListenr when the add button is click
//        ivToggleChoiceVisibility.setOnClickListener {
//            //lauchNewCardActivity = !lauchNewCardActivity
//            launchComposeView()
//        }

        deleteButton.setOnClickListener {

            deleteCard()
        }

        addButton.setOnClickListener {
            val intent = Intent(this, AddCardActivity::class.java)
            resultLauncher.launch(intent)
        }


   }

    @SuppressLint("SetTextI18n")
    private fun deleteCard() {
        val flashcardQuestionToDelete = flashcardQuestion.text.toString()
        flashcardDatabase.deleteCard(flashcardQuestionToDelete)

        // Update the allFlashcards list after deletion
        allFlashcards = flashcardDatabase.getAllCards().toMutableList()

        // Update currentCardDisplayedIndex if necessary
        if (currentCardDisplayIndex >= allFlashcards.size){
            currentCardDisplayIndex = 0
        }

        // Update the user interface with the new card
        if (allFlashcards.isNotEmpty()) {
            val (question,answer) = allFlashcards[currentCardDisplayIndex]
            flashcardAnswer.text = answer
            flashcardQuestion.text = question
        } else {
            // If there are no cards left, display an "empty" status
            flashcardAnswer.text = "Add a card!"
            flashcardQuestion.text = ""

        }

    }


//    private fun startAddCardActivity() {
//        // create an Intent to start AddCardActivity
//        val intent = Intent(this,AddCardActivity::class.java)
//
//        // Get the strings from the TextViews
//        val currentQuestion = flashcardQuestion.text.toString()
//        val currentAnswer = flashcardAnswer.text.toString()
//
//        // Add the current question and answer as extras to the Intent
//        intent.putExtra("question",currentQuestion)
//        intent.putExtra("answer",currentAnswer)
//        addCardActivityResultLauncher.launch(intent)
//    }

//    private fun startAddCardActivity() {
//        val intent = Intent(this,AddCardActivity::class.java)
//        val question = flashcardQuestion.text
//        val answer = flashcardAnswer.text
//        intent.putExtra("question", question)
//        intent.putExtra("answer", answer)
//        addCardActivityResultLauncher.launch(intent)
//    }

    private fun toggleAnswerVisibility() {
        if (flashcardAnswer.visibility == View.VISIBLE) {
            flashcardAnswer.visibility = View.INVISIBLE
            flashcardQuestion.visibility = View.VISIBLE
        } else {
            flashcardQuestion.visibility = View.INVISIBLE
            flashcardAnswer.visibility = View.VISIBLE
        }
    }

    // returns a random number between minNumber and maxNumber, inclusive.
// for example, if i called getRandomNumber(1, 3), there's an equal chance of it returning either 1, 2, or 3.
   private fun getRandomNumber(minNumber: Int, maxNumber: Int): Int {
        return (minNumber..maxNumber).random() // generated random from 0 to 10 included
    }

    private fun initializeViews() {
        flashcardQuestion = findViewById(R.id.flashcard_question)
        flashcardAnswer = findViewById(R.id.flashcard_answer)
        addButton = findViewById(R.id.add_button_image)
        editButton = findViewById(R.id.edit_button_image)
        nextButton = findViewById(R.id.next_button_image)
        deleteButton = findViewById(R.id.delete_button_image)
    }


    // This extracts any data that was passed back from AddCardActivity
    private val resultLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()){ result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val data = result.data

            if (data != null) {
                val question = data.extras?.getString("question")
                val answer = data.extras?.getString("answer")

                // Log the value of the strings for easier debugging
                Log.i("MainActivity", "question: $question")
                Log.i("MainActivity", "answer: $answer")

                flashcardQuestion.text = question
                flashcardAnswer.text = answer


                // Save newly created flashcard to database
                if (question !=null && answer !=null) {
                    flashcardDatabase.insertCard(Flashcard(question,answer))
                    // Update set of flashcards to include new card
                    allFlashcards = flashcardDatabase.getAllCards().toMutableList()

                } else {
                    Log.e("TAG","Missing question or answer to input into database. Question is $question and answer is $answer ")

                }

            } else {
                Log.i("MainActivity","Returned null data from AddCardActivity")
            }
        }
    }
    // Managing the results of the card modification activity
}

//        flashcardAnswer.setOnClickListener {
//            flashcardAnswer.visibility = View.INVISIBLE
//            flashcardQuestion.visibility = View.VISIBLE
//        }

//        val resultLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
//
//            // This code is executed in StartingActivity after we come back from EndingActivity
//
//            // This extracts any data that was passed back from EndingActivity
//            val data: Intent? = result.data
//            // ToDo: Execute more code here
//
//        }