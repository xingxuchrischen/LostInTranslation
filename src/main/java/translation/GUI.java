package translation;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class GUI {

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            // the helpers: one translates, two convert between codes and names
            Translator translator = new JSONTranslator();
            CountryCodeConverter countryConverter = new CountryCodeConverter();
            LanguageCodeConverter languageConverter = new LanguageCodeConverter();

            // language dropdown, filled with language names (not codes)
            List<String> languageNames = new ArrayList<>();
            for (String code : translator.getLanguageCodes()) {
                languageNames.add(languageConverter.fromLanguageCode(code));
            }
            Collections.sort(languageNames);
            JComboBox<String> languageComboBox = new JComboBox<>(languageNames.toArray(new String[0]));

            JPanel languagePanel = new JPanel();
            languagePanel.add(new JLabel("Language:"));
            languagePanel.add(languageComboBox);

            // label that shows the translation
            JLabel resultLabel = new JLabel("Translation:");
            JPanel resultPanel = new JPanel();
            resultPanel.add(resultLabel);

            // scrollable list of country names (not codes)
            List<String> countryNames = new ArrayList<>();
            for (String code : translator.getCountryCodes()) {
                countryNames.add(countryConverter.fromCountryCode(code));
            }
            Collections.sort(countryNames);
            JList<String> countryList = new JList<>(countryNames.toArray(new String[0]));
            countryList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
            JScrollPane scrollPane = new JScrollPane(countryList);

            // runs whenever the user picks something: turn names into codes, then translate
            Runnable updateTranslation = () -> {
                String countryName = countryList.getSelectedValue();
                String languageName = (String) languageComboBox.getSelectedItem();
                if (countryName == null || languageName == null) {
                    resultLabel.setText("Translation:");
                    return;
                }
                String countryCode = countryConverter.fromCountry(countryName);
                String languageCode = languageConverter.fromLanguage(languageName);
                String result = translator.translate(countryCode, languageCode);
                if (result == null) {
                    result = "no translation found!";
                }
                resultLabel.setText("Translation: " + result);
            };

            languageComboBox.addActionListener(e -> updateTranslation.run());
            countryList.addListSelectionListener(e -> {
                if (!e.getValueIsAdjusting()) {
                    updateTranslation.run();
                }
            });

            // layout: dropdown and translation on top, country list fills the rest
            JPanel topPanel = new JPanel();
            topPanel.setLayout(new BoxLayout(topPanel, BoxLayout.Y_AXIS));
            topPanel.add(languagePanel);
            topPanel.add(resultPanel);

            JPanel mainPanel = new JPanel(new BorderLayout());
            mainPanel.add(topPanel, BorderLayout.NORTH);
            mainPanel.add(scrollPane, BorderLayout.CENTER);

            JFrame frame = new JFrame("Country Name Translator");
            frame.setContentPane(mainPanel);
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setSize(400, 350);
            frame.setLocationRelativeTo(null);
            frame.setVisible(true);
        });
    }
}
