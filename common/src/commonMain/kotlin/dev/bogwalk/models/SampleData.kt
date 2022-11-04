package dev.bogwalk.models

const val review = "Lorem ipsum dolor sit amet, consectetur adipiscing elit. Fusce volutpat luctus interdum."
val references = listOf(
    "Kotlin" to "https://kotlinlang.org/", "JetBrains" to "https://www.jetbrains.com/", "GitHub" to "https://github.com/"
)

val q1 = Question(
    1, "Which animal is NOT affected by foot & mouth disease?",
    listOf("Cattle", "Sheep", "Horse", "Pig"), "Horse", null
)
val q2 = Question(
    2,
    "Which of the following is an appropriate & synergistic antibiotic combination for the treatment of Rhodococcus equi in foals?",
    listOf("Azithromycin + Rifampin", "Rifampin + Penicillin", "Penicillin + TMS", "TMS + Erythromycin"),
    "Azithromycin + Rifampin", null
)
val q3 = Question(
    3, "Which of the following species does not normally have a gall bladder?",
    listOf("Horse", "Dog", "Pig", "Cattle"), "Horse", null
)

val q4 = Question(
    4, "A left azygous vein is NOT present in which of the following species?",
    listOf("Cattle", "Sheep", "Horse", "Pig"), "Horse",
    Review(review.repeat(3), references)
)

val questionStorage = mutableListOf(q1, q2, q3)