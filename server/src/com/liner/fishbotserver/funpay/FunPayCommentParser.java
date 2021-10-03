package com.liner.fishbotserver.funpay;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class FunPayCommentParser {

    public static void getComments(Callback callback){
        List<FunPayComment> funPayComments = new ArrayList<>();
        getPageContent("https://funpay.ru/users/3426436/", new PageContentCallback() {
            @Override
            public void onObtained(String content, InputStream inputStream) {
                Elements reviews = Jsoup.parse(content).getElementsByClass("review-container");
                for(Element review:reviews){
                    Element element = review.getElementsByClass("review-compiled-review").get(0);
                    String photoUrl = "https://funpay.ru"+element.select("img").first().attr("src");
                    String date = element.getElementsByClass("review-item-date").first().text();
                    String detail = element.getElementsByClass("review-item-detail").first().text();
                    String text = element.getElementsByClass("review-item-text").first().text();
                    Pattern pattern = Pattern.compile("(rating([0-9]))");
                    Matcher matcher = pattern.matcher( element.getElementsByClass("rating").first().toString());
                    int rating = 0;
                    if(matcher.find()){
                        rating = Integer.parseInt(matcher.group(2));
                    }
                    funPayComments.add(new FunPayComment(
                            photoUrl,
                            date,
                            detail,
                            text,
                            rating
                    ));
                }
                callback.onParsed(funPayComments);
            }

            @Override
            public void onFailed(String reason) {
                callback.onParsed(funPayComments);
            }
        });
    }
    public interface Callback{
        void onParsed(List<FunPayComment> funPayCommentList);
    }

    private static void getPageContent(String pageUrl, PageContentCallback callback) {
        try {
            URLConnection urlConnection = new URL(pageUrl).openConnection();
            urlConnection.setConnectTimeout((int) TimeUnit.SECONDS.toMillis(30));
            urlConnection.setReadTimeout((int) TimeUnit.SECONDS.toMillis(30));
            urlConnection.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko)");
            InputStream inputStream = urlConnection.getInputStream();
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
            StringBuilder stringBuilder = new StringBuilder();
            String line;
            while ((line = bufferedReader.readLine()) != null)
                stringBuilder.append(line);
            callback.onObtained(stringBuilder.toString(), inputStream);
        } catch (IOException e) {
            callback.onFailed(e.getMessage());
        }
    }

    public interface PageContentCallback {
        void onObtained(String content, InputStream inputStream);

        void onFailed(String reason);
    }

    public static void main(String[] args) {
        getComments(funPayCommentList -> {
            System.out.println("Comments: "+funPayCommentList.size());
            for(FunPayComment comment:funPayCommentList){
                System.out.println(comment.toString());
            }
        });
    }
}
