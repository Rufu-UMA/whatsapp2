package com.example.whatsapp2.utils;

import java.util.Random;

public class Botkit {

    private static final String[] RESPUESTAS_ALEATORIAS = {
            "¡Eso suena genial!",
            "¿De verdad? Cuéntame más.",
            "Jajaja, ¡qué gracioso!",
            "No estoy seguro de qué pensar sobre eso.",
            "¡Totalmente de acuerdo!",
            "Mmm... interesante punto de vista.",
            "¡Vaya! No me lo esperaba.",
            "¿Y qué pasó después?",
            "Eso tiene mucho sentido.",
            "¡Qué locura!",
            "A veces pienso lo mismo.",
            "¡Me encanta!",
            "Oye, eso me recuerda a algo...",
            "Perdona, ¿puedes repetirlo?",
            "¡Increíble!",
            "Sí, claro.",
            "No sé yo...",
            "¡Exacto!",
            "Qué curioso.",
            "¡No me digas!",
            "Eso explica muchas cosas.",
            "¡Madre mía!",
            "Estoy flipando.",
            "Bueno, es lo que hay.",
            "¡Qué buena onda!",
            "Ni idea, la verdad.",
            "Podría ser...",
            "¡Menuda historia!",
            "Sigue, sigue.",
            "¡Qué fuerte!",
            "Jaja, sí.",
            "No lo veo claro.",
            "¡Qué pasada!",
            "Lo tendré en cuenta.",
            "¡Claro que sí!",
            "No me convence mucho.",
            "¡Bravo!",
            "¡Qué interesante!",
            "Nunca lo había pensado así.",
            "¡Ojalá!",
            "Eso espero.",
            "¡Qué risa!",
            "Es un poco raro, ¿no?",
            "¡Absolutamente!",
            "¡Qué bien!",
            "Me has dejado sin palabras.",
            "¡Qué coincidencia!",
            "No te preocupes.",
            "¡Ánimo!",
            "Ya veo.",
            "¡Qué sorpresa!",
            "Tiene su lógica.",
            "¡Venga ya!",
            "Es posible.",
            "¡Qué maravilla!",
            "Me alegro mucho.",
            "¡Qué pena!",
            "No me lo creo.",
            "¡Ojo ahí!",
            "Eso dicen.",
            "Sahi h"
    };

    public static String getResponse(String input) {
        if (input == null) return "...";
        
        String lowerInput = input.toLowerCase().trim();

        if (lowerInput.contains("hola") || lowerInput.contains("buenas")) {
            String[] saludos = {
                "¡Hola! ¿Qué tal todo?", 
                "¡Buenas! ¿Cómo va la cosa?", 
                "¡Hola! Me alegro de leerte.",
                "¡Hey! ¿Qué cuentas?"
            };
            return saludos[new Random().nextInt(saludos.length)];
        } else if (lowerInput.contains("tal") || lowerInput.contains("estas") || lowerInput.contains("estás")) {
            return "Aquí andamos, simulando ser inteligente. ¿Y tú?";
        } else if (lowerInput.contains("adios") || lowerInput.contains("chao") || lowerInput.contains("hasta luego")) {
            return "¡Nos vemos! Cuídate.";
        } else if (lowerInput.contains("nombre")) {
            return "Me llamo Botkit, un placer.";
        } else if (lowerInput.contains("gracias")) {
            return "¡De nada! Para eso estamos.";
        } else if (lowerInput.contains("?")) {
            String[] respuestasPregunta = {
                "Buena pregunta... déjame pensarlo.", 
                "No tengo la respuesta a eso ahora mismo.", 
                "Es complicado de responder.",
                "Quizás sí, quizás no."
            };
            return respuestasPregunta[new Random().nextInt(respuestasPregunta.length)];
        }
        
        Random random = new Random();
        return RESPUESTAS_ALEATORIAS[random.nextInt(RESPUESTAS_ALEATORIAS.length)];
    }
}
