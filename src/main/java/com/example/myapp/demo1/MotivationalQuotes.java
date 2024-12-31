package com.example.myapp.demo1;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class MotivationalQuotes {
    private Map<String, String[]> moodQuotes;
    private Random random;

    public MotivationalQuotes() {
        moodQuotes = new HashMap<>();
        random = new Random();
        initializeQuotes();
    }

    private void initializeQuotes() {
        moodQuotes.put("Happy 😊", new String[]{
                "Your joy is contagious. Keep spreading happiness!",
                "Happiness looks gorgeous on you!",
                "Your positive energy can move mountains."
        });

        moodQuotes.put("Sad 😢", new String[]{
                "Every storm runs out of rain. Brighter days are ahead.",
                "Your strength is greater than any struggle.",
                "This too shall pass. You are resilient and capable."
        });

        moodQuotes.put("Neutral 😐", new String[]{
                "Today is full of possibilities. Make it amazing!",
                "You have the power to turn an ordinary day into an extraordinary one.",
                "Embrace the day with an open mind and heart."
        });

        moodQuotes.put("Content", new String[]{
                "Contentment is the greatest form of wealth.",
                "Find joy in the journey, not just the destination.",
                "Appreciate this moment of peace. You've earned it."
        });

        moodQuotes.put("Stressed", new String[]{
                "Take a deep breath. You've got this.",
                "Stress is temporary. Your strength is permanent.",
                "One step at a time. You're making progress."
        });

        moodQuotes.put("Excited", new String[]{
                "Channel your excitement into amazing achievements!",
                "Your enthusiasm is the spark that ignites great things.",
                "Excitement is the fuel for your dreams. Keep that fire burning!"
        });

        moodQuotes.put("Tired", new String[]{
                "Rest if you must, but don't you quit.",
                "Your perseverance through fatigue is admirable. Keep going!",
                "Even when tired, you're still capable of great things."
        });

        moodQuotes.put("Angry", new String[]{
                "Channel your anger into positive change.",
                "Breathe deeply. This too shall pass.",
                "Your strength lies in how you handle difficult emotions."
        });

    }

    public String getQuoteForMood(String mood) {
        String[] quotes = moodQuotes.get(mood);
        if (quotes != null && quotes.length > 0) {
            return quotes[random.nextInt(quotes.length)];
        }
        return "Stay positive and keep moving forward!";
    }
}

