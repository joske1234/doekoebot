package com.team6.g.doekoebot;


import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

public class test {
    @Test
    public void test() {
        String s = "nicom::chart_with_downwards_trend::leotm::rage2::timer_clock::chart_with_downwards_trend::ambulance";

        for (String s1 : s.split("::")) {
            System.out.println(s1);
        }
    }
    
    @Test
    public void testsingle() {
        String s = "nicom";
        
        for (String s2 : s.split("::")) {
            System.out.println(s2);
        }
    }
    
    @Test
    public void skinToneEmoji() {
        String s  = "man::skin-tone-6::jamal::eggplant";
        List<String> emojis = new ArrayList<>();
        int emojiIndex = 0;
        
        for (String s1 : s.split("::")) {
            if (s1.startsWith("skin-tone")) {
                emojis.set(emojiIndex, emojis.get(emojiIndex) + "::" + s1);
                emojiIndex++;
            } else {
                emojis.add(s1);
            }
        }

        System.out.println(emojis.toString());
    }
}
