package com.example.tpairviafx;


import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Scanner;

/** Class for getting updated accurate conversion rates
 * @author  Bendeguz Kudor*/
    public class Currency {
        /**Field for the URL for the exchange rate API with the key*/

        private static final String api = "https://openexchangerates.org/api/latest.json?app_id=5738214a5d4041059d56b94f7893352c&base=USD&symbolGBP";
        // 5738214a5d4041059d56b94f7893352c Exchangerates api key
        //https://github.com/dneto/oer-java
        //https://www.youtube.com/watch?v=zZoboXqsCNw
        //https://github.com/stleary/JSON-java


        public Currency() {

        }

        public static void main(String[] args) throws IOException {
            Currency currency = new Currency();
            System.out.println(currency.getConversion(100)/100);
        }

        /**

         This method retrieves the conversion rate of GBP to the local currency from an external API,
         then uses the rate to convert the given amount from GBP to the local currency.
         @param amount the amount to convert, in GBP
         @return the converted amount, in the local currency
         @throws IOException if there is an error in connecting to the external API or if the API response does not contain the conversion rate
         */
        public double getConversion(double amount) throws IOException {
            URL url = new URL(api);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");

            int response = connection.getResponseCode();
            System.out.println(response);
            Scanner scanner = new Scanner(connection.getInputStream());
            String line;
            double rate = -1;
            while (scanner.hasNextLine()) {
                line = scanner.nextLine();

                if (line.contains("GBP")) {
                    int start = line.indexOf(":") + 1;
                    int end = line.indexOf(",", start);
                    rate = Double.parseDouble(line.substring(start, end));
                    break;
                }
            }
            scanner.close();
            connection.disconnect();
            if (rate == -1) {
                throw new IOException("GBP conversion rate not found in API response");
            }
            return amount * rate;
            }
        }




