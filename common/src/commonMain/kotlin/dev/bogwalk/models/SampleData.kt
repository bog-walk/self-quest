package dev.bogwalk.models

val q1 = Question(
    1, "Which animal is NOT affected by foot & mouth disease?",
    "Cattle", "Sheep", "Horse", "Pig", "Horse"
)
val q2 = Question(
    2,
    "Which of the following is an appropriate & synergistic antibiotic combination for the treatment of Rhodococcus equi in foals?",
    "Azithromycin + Rifampin", "Rifampin + Penicillin", "Penicillin + TMS",
    "TMS + Erythromycin", "Azithromycin + Rifampin"
)
val q3 = Question(
    3, "Which of the following species does not normally have a gall bladder?",
    "Horse", "Dog", "Pig", "Cattle", "Horse"
)

val q4 = Question(
    4, "A left azygous vein is NOT present in which of the following species?",
    "Cattle", "Sheep", "Horse", "Pig", "Horse"
)

val questionStorage = mutableListOf(q1, q2, q3)