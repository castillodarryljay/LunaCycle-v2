package com.example.ai

/**
 * LunaGeneralHealthKnowledge:
 * Comprehensive multi-domain knowledge base for Lifestyle, Infradian Fasting,
 * Micronutrients, Stress & HPA Axis, Conversational Small Talk, and Open-Domain Q&A.
 */
object LunaGeneralHealthKnowledge {

    fun findGeneralMatch(lower: String, ctx: ClinicalUserContext): String? {
        val citation = "\n\n*[Luna Ultra AI v4.0 • On-Device Wellness & Lifestyle Engine • 100% Private]*"

        // 1. GREETINGS & CASUAL INTERACTION
        if (containsAny(lower, "hello", "hi luna", "hey luna", "good morning", "good evening", "good afternoon", "who are you", "what can you do", "help me", "how are you")) {
            return """
                ### ✨ Hello ${ctx.userName}! I am Luna AI
                I am your private, on-device clinical endocrinology and menstrual health companion, running 100% locally on your smartphone.

                #### 📊 Your Real-Time Cycle Snapshot:
                • **Cycle Position:** Day **${ctx.cycleDay}** of your **${ctx.phase} Phase**
                • **Next Period Countdown:** In **${ctx.daysUntilPeriod} days** (Estimated: ${ctx.nextPeriodDate})
                • **Current Fertility Status:** **${ctx.fertilityStatus}**
                • **Logged Hydration:** ${ctx.todayWaterMl} ml / ${ctx.waterGoalMl} ml

                #### 💡 What You Can Ask Me:
                • **Cycle & Symptoms:** "Why do I have cramps?", "What does brown blood mean?", "Why am I bloated?"
                • **Clinical Conditions:** "Do I have PCOS?", "How to detect endometriosis?", "Is this a UTI or yeast infection?"
                • **Nutrition & Workouts:** "What should I eat in the luteal phase?", "Can I do HIIT today?", "Seed cycling rules"
                • **Fertility & Contraception:** "Can I get pregnant right now?", "Plan B vs Ella", "Missed birth control pill"
                • **Supplements & Labs:** "Which magnesium is best?", "What blood tests should I get?", "Thyroid panel"
                • **General Wellness:** "Why can't I sleep before my period?", "Intermittent fasting rules for women"

                What would you like to explore or check in on today?$citation
            """.trimIndent()
        }

        // 2. INTERMITTENT FASTING & DIETARY TIMING
        if (containsAny(lower, "fasting", "intermittent fast", "16/8", "fast like a girl", "can i fast", "omad", "fast during period", "fast during luteal")) {
            val fastingGuideline = when (ctx.phase) {
                "Follicular", "Ovulatory" -> """
                    • **Phase Status:** High systemic insulin sensitivity and robust metabolic resilience driven by rising 17β-estradiol.
                    • **Fasting Tolerance:** Moderate-to-extended intermittent fasting (14 to 16 hours, e.g., 16:8) is generally well tolerated. Estrogen buffers cellular stress and supports autophagy and hepatic glycogen depletion without triggering excessive hypothalamic stress.
                """.trimIndent()
                "Menstrual" -> """
                    • **Phase Status:** Energy baseline is lower; active uterine desquamation requires micronutrient support (heme iron, B12, vitamin C).
                    • **Fasting Tolerance:** Gentle 12–13 hour circadian fasting (eating within a 11–12 hour daylight window) is optimal. Avoid rigorous calorie restriction to allow tissue regeneration.
                """.trimIndent()
                else -> """
                    • **Phase Status:** Progesterone is the dominant steroid, requiring higher baseline glucose and circulating insulin to support corpus luteum maintenance.
                    • **Fasting Warning:** **Avoid prolonged fasting (>13–14 hours) during the late luteal phase!** Extended nutrient deprivation in this phase activates hypothalamic CRH and cortisol secretion, which competes with progesterone receptors, worsens PMS/PMDD, triggers severe sleep fragmentation, and can delay or impair normal menstruation.
                """.trimIndent()
            }

            return """
                ### ⏱️ Clinical Chrono-Nutrition: Intermittent Fasting & Infradian Rhythm
                **Patient:** ${ctx.userName} | **Current Phase:** Day ${ctx.cycleDay} (${ctx.phase} Phase)

                #### 🧬 Metabolic Endocrinology of Female Fasting:
                Unlike the male 24-hour circadian cycle, female metabolic physiology is modulated by a **28-day infradian rhythm**. Kisspeptin neurons in the female hypothalamus are extraordinarily sensitive to energy availability (AMPK/mTOR pathways):
                $fastingGuideline

                #### 🥗 Practical Recommendations:
                1. **Break Fast with Protein & Healthy Lipids:** Prioritize 30g of protein (pastured eggs, wild fish, collagen) paired with avocado or olive oil to avoid a reactive glucose spike.
                2. **Hydration with Electrolytes:** Fasting increases renal sodium excretion. Ensure you hit your ${ctx.waterGoalMl} ml water target with a pinch of unrefined sea salt or potassium citrate.$citation
            """.trimIndent()
        }

        // 3. SUPPLEMENTS, VITAMINS & MICRONUTRIENTS
        if (containsAny(lower, "supplements", "vitamins", "magnesium", "iron", "vitamin d", "inositol", "omega 3", "fish oil", "zinc", "b6", "coq10", "ashwagandha", "vitex", "chasteberry")) {
            return """
                ### 💊 Clinical Micronutrient & Evidence-Based Supplement Guide
                **Patient:** ${ctx.userName} | **Current Phase:** Day ${ctx.cycleDay} (${ctx.phase})

                #### 🔬 Core Supplements for Menstrual & Hormonal Balance:
                1. **Magnesium (Elemental 300–400 mg daily):**
                   • **Magnesium Glycinate:** Highly bioavailable, bound to glycine for neuro-calming, GABA support, dysmenorrhea relief, and deep slow-wave sleep.
                   • **Magnesium L-Threonate:** Uniquely crosses the blood-brain barrier; preferred for premenstrual migraines and cognitive focus.
                   • **Magnesium Citrate:** Osmotic laxative; ideal for progesterone-induced luteal constipation.
                2. **Iron Bisglycinate (25–50 mg elemental):** Chelate form that avoids gastrointestinal nausea and constipation caused by ferrous sulfate. Always pair with Vitamin C (Ascorbic acid) to reduce ferric to absorbable ferrous ions.
                3. **Vitamin D3 (2,000–5,000 IU) + K2 (MK-7 100 mcg):** Vitamin D acts as a nuclear steroid hormone, modulating ovarian AMH expression and immune surveillance. Vitamin K2 directs calcium to bone matrices rather than arterial walls.
                4. **Omega-3 Fatty Acids (EPA/DHA 1,500–2,000 mg):** Directly displaces arachidonic acid in uterine cell membranes, reducing pro-inflammatory series-2 prostaglandins (PGF2α) that cause menstrual cramps.
                5. **Myo- & D-Chiro Inositol (40:1 ratio, 2,000–4,000 mg):** Gold standard for insulin sensitization, follicle maturation, and PCOS management.
                6. **Active Vitamin B6 (Pyridoxal-5-Phosphate / P5P 50 mg):** Essential enzymatic cofactor for converting tryptophan into serotonin and dopamine, relieving luteal mood dysphoria and mastalgia.$citation
            """.trimIndent()
        }

        // 4. STRESS, CORTISOL, ADRENAL HEALTH & BURNOUT
        if (containsAny(lower, "stress", "cortisol", "adrenal", "burnout", "high cortisol", "belly fat", "anxiety attack", "panic", "breath", "relax", "nervous system")) {
            return """
                ### 🩺 Clinical Neuroendocrinology: Stress, Cortisol & The HPA Axis
                **Patient:** ${ctx.userName} | **Current Phase:** Day ${ctx.cycleDay} (${ctx.phase})

                #### 🧬 The "Progesterone Steal" Myth vs. Cortisol Receptor Competition:
                Chronic psychological or physiologic stress activates the **Hypothalamic-Pituitary-Adrenal (HPA) axis**, flooding systemic circulation with cortisol and epinephrine:
                1. **Hypothalamic GnRH Suppression:** High cortisol directly suppresses the hypothalamic GnRH pulse generator, blunting pituitary LH and FSH release. This arrests follicle development and delays ovulation.
                2. **Glucocorticoid Receptor Competition:** At target tissues (endometrium, brain, breast tissue), cortisol competitively binds and antagonizes progesterone receptors, magnifying estrogen dominance symptoms even if serum progesterone is technically normal.
                3. **Visceral Adiposity:** Elevated nighttime cortisol combined with compensatory insulin spikes directs lipid storage to intra-abdominal visceral adipose tissue ("cortisol belly").

                #### 🧘 Real-Time Autonomic Downregulation Protocol:
                1. **Cyclic Physiological Sighing:** Two consecutive rapid nasal inhales followed by one prolonged, unforced mouth exhale. Performing 3 cycles instantly activates pulmonary stretch receptors and stimulates the vagus nerve, lowering heart rate within 30 seconds.
                2. **Morning Solar Photon Capture:** View natural outdoor sunlight for 10–15 minutes within 60 minutes of waking to anchor the cortisol awakening response (CAR) and circadian melatonin timing.
                3. **Phosphatidylserine (100–300 mg at dinner):** Blunts excessive ACTH-driven evening cortisol secretion for restorative sleep.$citation
            """.trimIndent()
        }

        // 5. ALCOHOL & CAFFEINE IMPACTS
        if (containsAny(lower, "alcohol", "wine", "beer", "cocktail", "drinking during period", "hangover period", "can i drink alcohol")) {
            return """
                ### 🍷 Clinical Toxicology: Alcohol Kinetics Across the Cycle
                **Patient:** ${ctx.userName} | **Current Phase:** Day ${ctx.cycleDay} (${ctx.phase})

                #### 🧬 Hepatic & Endocrine Interactions:
                1. **Hepatic Estrogen Clearance Impairment:** The liver prioritizes the detoxification of ethanol into acetaldehyde via alcohol dehydrogenase (ADH). During active alcohol metabolism, hepatic cytochrome P450 enzymes temporarily pause the hydroxylation and methylation of circulating estrogen, resulting in a transient spike in serum estradiol and worsening premenstrual breast tenderness and mood volatility.
                2. **REM Sleep Fragmentation:** While alcohol acts as an acute GABA agonist facilitating sleep onset, its nocturnal clearance suppresses rapid eye movement (REM) sleep and elevates nighttime resting heart rate by 8–15 bpm.
                3. **Prostaglandin Aggravation:** Alcohol promotes systemic dehydration and stimulates mast cell degranulation, which can intensify uterine dysmenorrhea and pelvic cramping the following morning.

                #### 💡 Harm Reduction Guidelines:
                • If consuming alcohol, drink 1 glass of mineral water with electrolytes between each beverage.
                • Take 300 mg of Magnesium Glycinate and 600 mg of N-Acetylcysteine (NAC) prior to bedtime to support hepatic glutathione regeneration.$citation
            """.trimIndent()
        }

        // 6. "PERIOD FLU" & IMMUNE FLUCTUATIONS
        if (containsAny(lower, "period flu", "sick before period", "chills before period", "fever period", "body aches period", "immune system period")) {
            return """
                ### 🩺 Clinical Immunology: "Period Flu" & Cyclical Inflammation
                **Patient:** ${ctx.userName} | **Current Phase:** Day ${ctx.cycleDay} (${ctx.phase})

                #### 🧬 Why Do You Feel Sick Before Your Period?
                "Period flu" is not caused by an infectious virus (such as influenza), but by a massive cascade of **endogenous inflammatory mediators** triggered by luteal hormone withdrawal:
                1. **Systemic Prostaglandin Spillover:** As the endometrial lining prepares to shed, high concentrations of prostaglandins (PGF2α and PGE2) escape into systemic pelvic and systemic circulation, inducing nausea, mild chills, gastrointestinal cramping, and diffuse musculoskeletal aching.
                2. **Progesterone-Mediated Immune Shift:** High luteal progesterone creates a state of mild systemic immunosuppression (to tolerate potential fetal implantation). When progesterone abruptly drops 24–48 hours prior to menses, a rebound inflammatory response occurs, elevating serum IL-6 and TNF-α.
                3. **Thermoregulatory Reset:** Progesterone withdrawal causes the hypothalamic thermoregulatory set-point to drop by ~0.4°C, producing sensations of shivering or hot-cold flashes.

                #### 💊 Clinical Relief Protocol:
                • **Curcumin with Piperine (500–1,000 mg):** Natural selective COX-2 inhibitor, reducing systemic prostaglandin synthesis without gastric mucosal damage.
                • **Hydration:** You have logged ${ctx.todayWaterMl} ml of water. Drink warm ginger or chamomile tea to settle gut inflammation.$citation
            """.trimIndent()
        }

        // 7. MOTIVATION, TIPS & HEALTH FACTS
        if (containsAny(lower, "tip", "fact", "joke", "motivation", "inspire", "quote", "tell me something")) {
            return """
                ### 🌸 Luna Daily Wellness Insight & Biological Fact
                **Cycle Position:** Day **${ctx.cycleDay}** of your **${ctx.phase} Phase**

                #### 💡 Fascinating Endocrinology Fact:
                Did you know that the female human heart actually changes its resting rate rhythmically across your 28-day cycle? During your luteal phase (post-ovulation), elevated progesterone increases your basal metabolic rate and body temperature, typically causing your **resting heart rate to rise by 2 to 5 beats per minute** compared to your follicular phase!

                #### 💖 Daily Empowerment:
                Your cycle is not an inconvenience—it is your **fifth vital sign** (recognized by the American College of Obstetricians and Gynecologists). Every phase gives you a distinct neurochemical superpower:
                • **Menstrual:** Intuition, deep reflection, and restorative recalibration.
                • **Follicular:** Creative brainstorming, cognitive speed, and high physical resilience.
                • **Ovulatory:** Peak verbal eloquence, magnetic connection, and peak strength.
                • **Luteal:** Attention to detail, boundary setting, and organization.

                Honor where your body is today. What can I help you discover next?$citation
            """.trimIndent()
        }

        return null
    }

    private fun containsAny(text: String, vararg keywords: String): Boolean {
        return keywords.any { text.contains(it, ignoreCase = true) }
    }
}
