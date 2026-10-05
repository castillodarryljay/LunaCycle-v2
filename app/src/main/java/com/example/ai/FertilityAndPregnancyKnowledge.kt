package com.example.ai

import com.example.data.model.UserEntity
import com.example.engine.PredictionResult

/**
 * Specialized Clinical Knowledge Domain:
 * Early Pregnancy, Implantation, hCG Kinetics, Conception Optimization & Postpartum.
 */
object FertilityAndPregnancyKnowledge {

    fun matchAndRespond(
        query: String,
        user: UserEntity,
        prediction: PredictionResult,
        cycleDayInt: Int,
        citation: String
    ): String? {
        val lower = query.lowercase().trim()

        // 1. Implantation Bleeding vs Menstrual Flow
        if (containsAny(lower, "implantation", "spotting before period", "implantation bleeding", "pink discharge", "brown spotting")) {
            return """
                ### 🩺 Clinical Reproductive Medicine: Implantation Bleeding vs. Menstrual Menses
                **Patient:** ${user.name.ifBlank { "Patient" }} | Day $cycleDayInt (${prediction.phase} Phase)

                #### 🧬 Differential Diagnostic Matrix:
                | Diagnostic Factor | Implantation Bleeding | Typical Menstrual Flow |
                | :--- | :--- | :--- |
                | **Timing** | 6–12 days post-ovulation (DPO) | Expected date of next period (Day 1) |
                | **Duration** | Transient (few hours to 24–48 hours max) | Sustained (3–7 consecutive days) |
                | **Color & Viscosity** | Light pink or light rust-brown | Bright red to dark crimson red |
                | **Volume** | Scant spotting (only visible when wiping) | Moderate to heavy (>20–80 mL) |
                | **Clots / Tissue** | Strictly NO clots or tissue fragments | Small fibrin clots frequently present |
                | **Uterine Cramping** | Mild, focal, transient twinges | Rhythmic, intense myometrial contractions |

                #### 🔬 Embryological Mechanism:
                During blastocyst implantation, trophoblastic syncytiotrophoblasts breach the endometrial epithelial surface and invade superficial decidual venules and spiral arterioles, causing localized micro-hemorrhage.

                #### 🧪 Pregnancy Testing Recommendation:
                Urine pregnancy tests require a human Chorionic Gonadotropin (hCG) concentration of ≥10–25 mIU/mL. Testing during implantation bleeding will often yield a false-negative because syncytiotrophoblasts have only just begun secreting hCG into maternal circulation. **Test 3–4 days after spotting resolves or on the first day of your missed period.**$citation
            """.trimIndent()
        }

        // 2. Early Pregnancy Signs & Testing Timeline
        if (containsAny(lower, "am i pregnant", "pregnancy test", "pregnancy signs", "early pregnancy", "hcg", "missed period pregnant")) {
            return """
                ### 🩺 Clinical Obstetrics: Early Gestational Signs & hCG Kinetics
                **Patient:** ${user.name.ifBlank { "Patient" }} | Day $cycleDayInt (${prediction.phase} Phase)

                #### 📈 hCG Secretion Dynamics:
                1. **Production:** Syncytiotrophoblasts secrete hCG into maternal circulation immediately post-implantation.
                2. **Doubling Time:** In early viable intrauterine gestation, serum hCG levels double every **48 to 72 hours**.
                3. **Home Urine Pregnancy Tests:**
                   • High-sensitivity early tests (10 mIU/mL threshold): Can detect hCG as early as 10–12 days post-ovulation (DPO).
                   • Standard tests (25 mIU/mL threshold): 99% accuracy when taken on or after the first day of the missed period.
                   • **Testing Golden Rule:** Always test with **first-morning urine**, which contains the highest concentration of hCG.

                #### 🧬 Earliest Physiological Indicators:
                • **Basal Body Temperature (BBT) Plateau:** A sustained thermal shift exceeding **18 consecutive days** without a menstrual temperature drop is a strong clinical indicator of successful implantation.
                • **Montgomery's Glands & Areolar Changes:** Early progesterone and hCG induce hypertrophy of Montgomery tubercles on the areola.
                • **Cervical Softening (Goodell's Sign):** Increased vascularity and mucosal congestion soften the cervical stroma.$citation
            """.trimIndent()
        }

        // 3. Conception Optimization & Fertile Window Mechanics
        if (containsAny(lower, "trying to conceive", "ttc", "get pregnant fast", "best days to get pregnant", "conception tips", "fertility window")) {
            return """
                ### 👶 Clinical Fertility Science: Conception Optimization Protocol
                **Patient:** ${user.name.ifBlank { "Patient" }} | Projected Ovulation: ${prediction.ovulationDate}

                #### 🎯 The Biological 6-Day Fertile Interval:
                The probability of clinical conception varies significantly across cycle days relative to ovulation (Day 0):
                • **O-5 to O-4:** 10–15% probability
                • **O-3 to O-2:** 20–25% probability
                • **O-1 (Day before ovulation):** **33% peak conception probability!**
                • **Day of Ovulation (O):** ~15% (Oocyte viability is strictly 12–24 hours).

                #### 🔬 Clinical Recommendations for TTC:
                1. **Coital Frequency:** Intercourse every **1 to 2 days** throughout the 5 days prior to predicted ovulation maintains optimal progressive motile sperm density in cervical crypt reservoirs.
                2. **Fertility-Friendly Lubricants:** Standard OTC lubricants (and saliva) are spermitoxic. Use hydroxyethylcellulose-based or isotonic lubricants that match fertile cervical mucus pH (7.2–7.8).
                3. **Nutritional Micronutrients for Oocyte Quality:**
                   • **CoQ10 (Ubiquinol 200–400 mg daily):** Supports mitochondrial ATP generation in maturing oocytes.
                   • **Methylfolate (800 mcg):** Superior to synthetic folic acid for neural tube synthesis.
                   • **Omega-3 (DHA/EPA 1000 mg):** Enhances uterine perfusion and blastocyst receptivity.$citation
            """.trimIndent()
        }

        // 4. Postpartum, Lactational Amenorrhea & Breastfeeding
        if (containsAny(lower, "postpartum", "breastfeed", "lactat", "lochia", "baby period", "nursing period", "lam")) {
            return """
                ### 🩺 Clinical Postpartum & Lactational Endocrinology
                **Patient:** ${user.name.ifBlank { "Patient" }}

                #### 🧬 Lactational Amenorrhea Method (LAM):
                1. **Prolactin-GnRH Axis:** Infant suckling stimulates mechanoreceptors in the nipple-areolar complex, transmitting neural impulses to the hypothalamus. This suppresses the pulsatile release of **Gonadotropin-Releasing Hormone (GnRH)**, consequently preventing the pituitary LH surge and inhibiting follicular maturation and ovulation.
                2. **LAM Contraceptive Criteria (98% efficacy if ALL met):**
                   • Infant is less than 6 months of age.
                   • Mother remains completely amenorrheic (no vaginal bleeding post-lochia).
                   • Exclusive or near-exclusive breastfeeding (intervals ≤4 hours by day, ≤6 hours at night, no formula or solid supplements).
                3. **Ovulation Precedes Bleeding:** The first ovulation after childbirth can occur **before** the first postpartum menstrual bleed, meaning pregnancy is possible prior to menses resumption.

                #### 🩸 Lochia Progression Timeline:
                • **Lochia Rubra (Days 1–4):** Dark red blood and decidual tissue fragments.
                • **Lochia Serosa (Days 5–10):** Pinkish-brown serous transudate.
                • **Lochia Alba (Days 10–28+):** Creamy yellowish-white discharge composed of leukocytes, mucus, and epithelial cells.$citation
            """.trimIndent()
        }

        return null
    }

    private fun containsAny(text: String, vararg keywords: String): Boolean {
        return keywords.any { text.contains(it, ignoreCase = true) }
    }
}
