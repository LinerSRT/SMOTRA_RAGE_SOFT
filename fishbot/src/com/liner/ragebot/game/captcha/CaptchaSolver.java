package com.liner.ragebot.game.captcha;

import com.sun.jna.platform.win32.WinDef;
import net.objecthunter.exp4j.Expression;
import net.objecthunter.exp4j.ExpressionBuilder;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.liner.ragebot.jna.JNAUtils.IUSER32;
import static com.sun.jna.platform.win32.WinUser.WM_CHAR;

public class CaptchaSolver {
    private static final Pattern patternGen = Pattern.compile("/captcha/gen\\.php\\?code=([0-9]{4})");
    private static final Pattern patternMath = Pattern.compile("/captcha/math\\.php\\?code=([0-9-+*/\\S]*)");
    private static final Pattern patternWord = Pattern.compile("/captcha/word\\.php\\?code=([0-9a-zA-Z\\S]*)");

    public static List<Integer> getKeyCodes(String data) {
        List<Integer> keyCodes = new ArrayList<>();
        String answer;
        Matcher genMatch = patternGen.matcher(data);
        Matcher mathMatch = patternMath.matcher(data);
        Matcher wordMatch = patternWord.matcher(data);
        if (genMatch.find()) {
            answer = genMatch.group(1);
            for (char aChar : answer.toCharArray())
                keyCodes.add(0xff & aChar);
        } else if (mathMatch.find()) {
            String filter = mathMatch.group(1);
            filter = filter.replace("%28", "(");
            filter = filter.replace("%29", ")");
            filter = filter.replace("%2A", "*");
            filter = filter.replace("%2B", "+");
            filter = filter.replace("%2D", "-");
            filter = filter.replace("%2F", "/");
            Expression expression = new ExpressionBuilder(filter).build();
            answer = String.valueOf((int) (Math.round(expression.evaluate())));
            for (char aChar : answer.toCharArray()) {
                keyCodes.add(0xff & aChar);
            }
        } else if (wordMatch.find()) {
            answer = wordMatch.group(1);
            for (char aChar : answer.toCharArray())
                keyCodes.add(0xff & aChar);
        }
        return keyCodes;
    }

    public static void inputCaptcha(WinDef.HWND window, String data) {
        List<Integer> keycodes = getKeyCodes(data);
        for (Integer keyCode : keycodes)
            IUSER32.PostMessage(window, WM_CHAR, new WinDef.WPARAM(keyCode), null);
    }

}
