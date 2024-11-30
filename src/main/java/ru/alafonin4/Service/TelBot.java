package ru.alafonin4.Service;

import com.vdurmont.emoji.Emoji;
import org.apache.poi.ss.usermodel.*;
import org.telegram.telegrambots.meta.api.methods.groupadministration.GetChatMember;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.File;
import org.telegram.telegrambots.meta.api.objects.chatmember.ChatMember;
import ru.alafonin4.Button;
import ru.alafonin4.Entity.*;
import ru.alafonin4.Entity.User;
import ru.alafonin4.KeyboardMarkupBuilder;
import com.vdurmont.emoji.EmojiParser;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.GetFile;
import org.telegram.telegrambots.meta.api.methods.commands.SetMyCommands;
import org.telegram.telegrambots.meta.api.methods.send.SendDocument;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.objects.*;
import org.telegram.telegrambots.meta.api.objects.commands.BotCommand;
import org.telegram.telegrambots.meta.api.objects.commands.scope.BotCommandScopeDefault;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import ru.alafonin4.Repository.*;
import ru.alafonin4.config.BotConfig;

import java.io.*;
import java.net.URL;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@Service
public class TelBot extends TelegramLongPollingBot {
    private static final String MODERATION = EmojiParser.parseToUnicode(":page_facing_up:" + "модерация");
    private static final String EMPLOYEE = EmojiParser.parseToUnicode(":page_facing_up:" + "управление сотрудниками");
    private static final String REPORT = EmojiParser.parseToUnicode(":page_facing_up:" + "отчет по проекту");
    private static final String PROGRAMM = EmojiParser.parseToUnicode(":page_facing_up:" + "программа");
    private static final String CHANGE = EmojiParser.parseToUnicode(":page_facing_up:" + "изменение списка товаров");
    private static final String CHANGETEXT = EmojiParser.parseToUnicode(":page_facing_up:" + "изменение текстов бота");
    private static final String SENDORDERIMAGE = EmojiParser.parseToUnicode(":page_facing_up:" + "отправить скрин заказа");
    private static final String ASKTOLIC = EmojiParser.parseToUnicode(":page_facing_up:" + "Задать вопрос");
    private static final String PRODUCT = EmojiParser.parseToUnicode(":page_facing_up:" + "Продукты");
    private static final String SENDREVIEWIMAGE = EmojiParser.parseToUnicode(":page_facing_up:" + "отправить скрин отзыва");
    //private static final String CHANGE = EmojiParser.parseToUnicode(":page_facing_up:" + "изменение списка товаров");
    private static final String BeginnerRuss = "Совет: Начни с изучения базовой лексики и простых грамматических конструкций. " +
            "Обрати внимание на основные глаголы (to be, have, do) и времена. " +
            "Практикуй простые разговорные фразы, слушай аудиозаписи и повторяй за носителями. " +
            "Тебе помогут ежедневные занятия по 15–30 минут. " +
            "Регулярное повторение основ создаст крепкий фундамент для дальнейшего обучения.";
    private static final String BeginnerTajik = "Маслиҳат: Аз омӯхтани луғатҳои асосӣ ва сохтори оддии грамматикӣ оғоз кун. " +
            "Ба глаголҳои асосӣ (to be, have, do) ва замонҳо диққат деҳ. " +
            "Ибораҳои гуфтугӯии оддиро машқ кун, аудиозаписҳоро гӯш кун ва пас аз гӯяндагон такрор кун. " +
            "Рӯзона барои омӯзиш 15–30 дақиқа вақт сарф кардан ба ту ёрӣ мерасонад. " +
            "Такрори мунтазам заминаи устувор барои рушди минбаъдаи ту хоҳад шуд.";
    private static final String ElementaryRuss = "Совет: Ты уже знаешь основы, поэтому стоит уделить внимание " +
            "расширению словарного запаса и изучению времен (например, настоящее и прошедшее время). " +
            "Смотри короткие видео на английском языке, старайся повторять услышанное, читай простые тексты и статьи. " +
            "Погружение в язык через аудирование и простое чтение поможет тебе быстрее прогрессировать.";
    private static final String PreIntermediateRuss = "Совет: На этом уровне важно развивать умение строить более " +
            "сложные предложения и освоить такие времена, как Present Perfect и Future Tenses. " +
            "Попробуй смотреть фильмы с субтитрами на английском, практикуй написание небольших текстов и " +
            "задавай себе вопросы на английском. Разговорная практика с носителями языка или " +
            "в языковых клубах также будет полезна.";
    private static final String IntermediateRuss = "Совет: Твой уровень уже достаточно хороший, но для улучшения нужно " +
            "развивать беглость речи и понимание сложных текстов. Начни читать книги и статьи на английском языке, " +
            "смотри фильмы без субтитров, участвуй в разговорах на различные темы. " +
            "Постепенно усложняй материал и учи сложные грамматические конструкции " +
            "(например, условные предложения и модальные глаголы).";
    private static final String UpperIntermediateRuss = "Совет: Ты уже можешь выражать свои мысли на английском, " +
            "но нужно увеличить точность и глубину владения языком. Попробуй вести дневник на английском, читай статьи " +
            "и книги на профессиональные темы, участвуй в обсуждениях и дискуссиях с носителями языка. " +
            "Развивай навыки понимания тонких смыслов в речи и письменных текстах, а также работай над произношением.";
    private static final String AdvancedRuss = "Совет: Поздравляю, ты владеешь языком на высоком уровне! " +
            "Тебе следует продолжать совершенствовать свой язык через специализированные материалы, " +
            "статьи и книги по твоим интересам. Погружение в профессиональную литературу и " +
            "практическое использование языка в повседневной жизни помогут сохранять и улучшать навыки. " +
            "Попробуй вести переговоры на английском или даже начать изучение английской литературы.";
    private static final String ElementaryTajik = "Маслиҳат: Ту аллакай асосҳоро медонӣ, ҳоло вақти он аст, ки луғати " +
            "худро васеъ намуда, замонҳои гуногунро (масалан, замони ҳозира ва гузашта) омӯзӣ. " +
            "Видеоҳои кӯтоҳро бо забони англисӣ тамошо кун, кӯшиш кун шунидаатро такрор кунӣ, матнҳои оддӣ ва мақолаҳо бихон. " +
            "Фурӯ рафтан ба муҳити забонӣ тавассути шунидан ва хондани осон ба ту барои пешравӣ кӯмак мекунад.";
    private static final String PreIntermediateTajik = "Маслиҳат: Дар ин сатҳ муҳим аст, ки тавонӣ ибораҳои " +
            "мураккабтар созӣ ва замонҳои мураккабро (мисли Present Perfect ва Future Tenses) омӯзӣ. " +
            "Филмҳоро бо субтитрҳо тамошо кун, кӯшиш кун ки матнҳои кӯтоҳ нависӣ ва ба худ саволҳо диҳӣ. " +
            "Машқҳои гуфтугӯӣ бо гӯяндагони забон ё дар клубҳои забонӣ ба ту фоида меоранд.";
    private static final String IntermediateTajik = "Маслиҳат: Сатҳи забони ту аллакай хуб аст, аммо барои беҳтар " +
            "шудан бояд равонии суханронӣ ва фаҳмиши матнҳои мураккабро такмил диҳӣ. " +
            "Китобҳо ва мақолаҳо бо забони англисӣ хон, филмҳоро бе субтитр тамошо кун, дар гуфтугӯҳо оид ба " +
            "мавзӯъҳои гуногун иштирок намо. Маводи худро тадриҷан мураккабтар кун ва сохторҳои грамматикии " +
            "мураккабро омӯз (масалан, шартҳои шартӣ ва феълҳои модалӣ).";
    private static final String UpperIntermediateTajik = "Маслиҳат: Ту аллакай фикрҳои худро ба таври равшан баён карда " +
            "метавонӣ, аммо бояд дақиқӣ ва амиқияти донишатро такмил диҳӣ. Дневникро бо забони англисӣ навис, " +
            "мақолаҳо ва китобҳоро дар мавзӯъҳои касбӣ хон, дар баҳсҳо ва муҳокимарониҳо бо гӯяндагони забон иштирок намо. " +
            "Малакаҳои фаҳмидани маъноҳои нозук дар суханронӣ ва матнҳоро такмил деҳ ва инчунин дар садонокӣ кор кун.";
    private static final String AdvancedTajik = "Маслиҳат: Табрик, ту ба сатҳи баланди забонӣ расидӣ! Ҳоло ба ту " +
            "лозим аст, ки малакаҳои худро тавассути маводи махсусгардонида такмил диҳӣ. " +
            "Мақолаҳо ва китобҳоро дар бораи соҳаҳои писандидаат хон ва забонро дар зиндагии рӯзмарра истифода кун. " +
            "Муваффақ хоҳӣ шуд, агар музокиротро бо забони англисӣ анҷом диҳӣ ва шояд ба омӯзиши адабиёти англисӣ оғоз кунӣ.";
    private static final String CHANNELNAME = "@mh_teaches";
    private static final String CHANNELLINK = "https://t.me/mh_teaches";
    private final int countOfQuestions = 25;
    private Boolean isStarted = false;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private TextOfMessageRepository textOfMessageRepository;
    @Autowired
    private QuestionRepository questionRepository;
    @Autowired
    private AnswerRepository answerRepository;
    @Autowired
    private ImageRepository imageRepository;
    @Autowired
    private TestService testService;
    @Autowired
    private WordRepository wordRepository;
    @Autowired
    private CurrentLanguageRepository currentLanguageRepository;
    @Autowired
    private UserAnswerRepository userAnswerRepository;
    @Autowired
    private UserTestSessionRepository userTestSessionRepository;

    private Map<Long, UserTestSession> activeSessions = new HashMap<>();
    private Map<Long, Question> enterQuestion = new HashMap<>();
    BotConfig config;

    public TelBot(BotConfig config) {
        this.config = config;
    }

    @Override
    public void onUpdateReceived(Update update) {
        /*var users = userRepository.findAll();
        for (var i:
                users) {
            i.setStageOfUsing(null);
            userRepository.save(i);
            i.setStageOfUsing(Stage.DoingNothing);
            userRepository.save(i);
        }*/
        // todo startScheduledMessage(LocalTime.of(8, 0));

        if (!isStarted) {
            startScheduledMessage(LocalTime.of(10, 0));
            isStarted = true;
        }
        if (update.hasMessage() && update.getMessage().hasDocument()) {
            processFile(update);
        }
        if (update.hasMessage() && update.hasPollAnswer()) {
            long chatId = update.getMessage().getChatId();
        }
        if (update.hasMessage() && update.getMessage().hasPhoto()) {
            photoProcessing(update);
        } else if (update.hasMessage() && update.getMessage().hasText()) {
            long chatId = update.getMessage().getChatId();
            //updateInfo(chatId);
            String messageText = update.getMessage().getText();
            if (!isUserSubscribed(chatId)) {
                sendMessageToSubscribeOnChannel(chatId);
                return;
            }

            User user = new User();
            if (!messageText.equals("/start")) {
                user = userRepository.findById(chatId).get();
            }

            if (messageText.equals("/start")) {
                registerUser(update.getMessage());
                user = userRepository.findById(chatId).get();
                //startScheduledMessage(chatId, LocalTime.of(8, 10));
            }

            if (user.getRole().equals(Role.Customer)) {
                getTextUpdateFromCustomer(update);
            } else if (user.getRole().equals(Role.Admin)) {
                getTextUpdateFromAdmin(update);
            }
        } else if (update.hasCallbackQuery()) {
            String callbackData = update.getCallbackQuery().getData();
            var m = update.getCallbackQuery().getMessage();
            long messageId = update.getCallbackQuery().getMessage().getMessageId();
            long chatId = update.getCallbackQuery().getMessage().getChatId();
            //updateInfo(chatId);

            User usern = userRepository.findById(chatId).get();
            if (usern.getRole().equals(Role.Customer)) {
                getCallBackQueryUpdateFromCustomer(update);
            } else if (usern.getRole().equals(Role.Admin)) {
                getCallBackQueryUpdateFromAdmin(update);
            }
        }
    }
    private void sendMessageToSubscribeOnChannel(long chatId) {
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(String.valueOf(chatId));
        sendMessage.setText("Минуточку. Чтобы пользоваться ботом подпишись на мой канал: \n" +
                "<a href='" + CHANNELLINK + "'>MH’s English Hub\uD83C\uDDFA\uD83C\uDDF8</a>");
        sendMessage.disableWebPagePreview();
        sendMessage.setParseMode("HTML");
        List<List<Button>> buttons = new ArrayList<>();
        List<Button> row1 = new ArrayList<>();
        row1.add(new Button("MH’s English Hub\uD83C\uDDFA\uD83C\uDDF8", "link", CHANNELLINK));
        List<Button> row2 = new ArrayList<>();
        row2.add(new Button("Я подписался", "I_Subscribed"));
        buttons.add(row1);
        buttons.add(row2);
        sendMessage.setReplyMarkup(KeyboardMarkupBuilder.setKeyboardWithRaw(buttons));

        try {
            execute(sendMessage);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    public void startScheduledMessage(LocalTime targetTime) {
        ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);

        LocalDateTime now = LocalDateTime.now();
        LocalDateTime firstRun = now.with(targetTime);

        if (now.isAfter(firstRun)) {
            firstRun = firstRun.plusDays(1);
        }

        long initialDelay = Duration.between(now, firstRun).toMillis();

        List<Word> listOfWords = (List<Word>) wordRepository.findAll();
        Random random = new Random();
        int number = random.nextInt(listOfWords.size());
        if (number < 0) {
            return;
        }
        Runnable task = () -> sendPeriodicMessage(listOfWords.get(number));

        scheduler.scheduleAtFixedRate(task, initialDelay, TimeUnit.DAYS.toMillis(2), TimeUnit.MILLISECONDS);
    }

    private void sendPeriodicMessage(Word word) {
        if (word == null) {
            return;
        }
        List<User> users = (List<User>) userRepository.findAll();
        String text = word.getWordInEnglish() + " " + word.getTranscription() + " "
                + word.getWordInRussian() + " " + word.getWordInTajik();
        for (var u:
             users) {
            SendMessage message = new SendMessage();
            message.setChatId(String.valueOf(u.getChatId()));
            message.setText(text);

            try {
                execute(message);
            } catch (TelegramApiException e) {
                e.printStackTrace();
            }
        }
    }
    private String normalizeUsername(String username) {
        if (username.startsWith("@")) {
            return username.substring(1);
        }
        return username;
    }
    private void getTextUpdateFromCustomer(Update update) {
        long chatId = update.getMessage().getChatId();
        String messageText = update.getMessage().getText();

        User user = new User();
        if (!messageText.equals("/start")) {
            user = userRepository.findById(chatId).get();
        }
        if (messageText.equals(SENDORDERIMAGE)) {
            System.out.println("hi");
        } else {
            switch (messageText) {
                case "Начать тест \uD83D\uDCDD":
                case "Оғози тест \uD83D\uDCDD":
                    startTest(chatId);
                    break;
                case "/start":
                    startFunc(update, chatId);
                    break;
                case "Обо мне \uD83D\uDCD6":
                case "Дар бораи ман \uD83D\uDCD6":
                case "/about":
                    about(chatId);
                    break;
                case "Поддержка \uD83D\uDCAC":
                case "Тамос гирифтан \uD83D\uDCAC":
                case "/support":
                    support(chatId);
                    break;
                case "Тоҷикӣ \uD83C\uDDF9\uD83C\uDDEF":
                case "Русский \uD83C\uDDF7\uD83C\uDDFA":
                    changeCurrentLanguage(chatId);
                    break;
                default:
                    sendMessage(chatId, "Извините, команда не распознана.");
                    break;
            }
        }
    }
    private void getCallBackQueryUpdateFromCustomer(Update update) {
        String callbackData = update.getCallbackQuery().getData();
        var m = update.getCallbackQuery().getMessage();
        Integer messageId = update.getCallbackQuery().getMessage().getMessageId();
        long chatId = update.getCallbackQuery().getMessage().getChatId();

        if (callbackData.equals("startTest")) {
            startTest(chatId);
            return;
        }

        if (callbackData.startsWith("answer_")) {
            var str = callbackData.split("_");
            long id = Long.parseLong(str[1]);
            UserTestSession testSession = activeSessions.get(chatId);
            testService.processUserAnswer(chatId, testSession, id); // Передаем сессию

            askCurrentQuestion(chatId, testSession, messageId);
        }
    }
    private void getTextUpdateFromAdmin(Update update) {
        long chatId = update.getMessage().getChatId();
        String messageText = update.getMessage().getText();

        User user = new User();
        if (!messageText.equals("/start")) {
            user = userRepository.findById(chatId).get();
        }
        if (messageText.equals(SENDORDERIMAGE)) {
            System.out.println("hi");
        } else {
            switch (messageText) {
                case "Начать тест \uD83D\uDCDD":
                case "Оғози тест \uD83D\uDCDD":
                    startTest(chatId);
                    break;
                case "/start":
                    startFunc(update, chatId);
                    break;
                case "Обо мне \uD83D\uDCD6":
                case "Дар бораи ман \uD83D\uDCD6":
                case "/about":
                    about(chatId);
                    break;
                case "/addAdmin":
                    onboard(chatId);
                    break;
                case "Редактировать":
                case "Ред":
                case "/change":
                    change(chatId);
                    break;
                case "/addQuestion":
                    addQuestion(chatId);
                    break;
                case "Добавить вопросы":
                case "Вопросы":
                case "/addQuestions":
                    addQuestions(chatId);
                    break;
                case "Добавить слова":
                case "Слова":
                case "/addWords":
                    addWords(chatId);
                    break;
                case "Поддержка \uD83D\uDCAC":
                case "Тамос гирифтан \uD83D\uDCAC":
                case "/support":
                    support(chatId);
                    break;
                case "Отчет по боту":
                case "Отчет":
                case "/report":
                    getReport(chatId);
                    break;
                case "Тоҷикӣ \uD83C\uDDF9\uD83C\uDDEF":
                case "Русский \uD83C\uDDF7\uD83C\uDDFA":
                    changeCurrentLanguage(chatId);
                    break;
                default:
                    if (user.getStageOfUs().equals(Stage.EnterNewAdminUser.toString()) && !messageText.startsWith("/")) {
                        var us = userRepository.findByUserName(normalizeUsername(messageText)).get();
                        us.setRole(Role.Admin);
                        userRepository.save(us);
                        setUserCommands(us.getChatId());
                        SendMessage sendMessage = new SendMessage();
                        sendMessage.setChatId(String.valueOf(chatId));
                        sendMessage.setText("Вы назначены админом");
                        sendMessage.setReplyMarkup(KeyboardMarkupBuilder.setReplyKeyboardWithRaw(getKeyboardForAdmin(chatId)));
                        try {
                            execute(sendMessage);
                        } catch (TelegramApiException e) {
                            e.printStackTrace();
                        }
                        break;
                    } else if (user.getStageOfUs().equals(Stage.EnterQuestionToAdd.toString()) && !messageText.startsWith("/")) {
                        Question q = new Question();
                        q.setText(messageText);
                        var us = userRepository.findById(chatId).get();
                        us.setStageOfUs(String.valueOf(Stage.EnterDifficultyOfQuestion));
                        userRepository.save(us);
                        enterQuestion.put(chatId, q);
                        sendMessage(chatId, "Введи сложность вопроса от 1 до 6.");
                        break;
                    } else if (user.getStageOfUs().equals(Stage.EnterDifficultyOfQuestion.toString()) && !messageText.startsWith("/")) {
                        Question q = enterQuestion.get(chatId);
                        q.setDifficulty(Integer.parseInt(messageText));
                        q.setCreatedAt(LocalDateTime.now());
                        var qu = questionRepository.save(q);
                        var us = userRepository.findById(chatId).get();
                        us.setStageOfUs(String.valueOf(Stage.EnterRightAnswerToAdd));
                        userRepository.save(us);
                        enterQuestion.put(chatId, qu);
                        sendMessage(chatId, "Введи правильный ответ.");
                        break;
                    } else if (user.getStageOfUs().equals(Stage.EnterRightAnswerToAdd.toString()) && !messageText.startsWith("/")) {
                        Answer answer = new Answer();
                        answer.setAnswer(messageText);
                        answer.setQuestion(enterQuestion.get(chatId));
                        answer.setIsRight(true);
                        answerRepository.save(answer);
                        var us = userRepository.findById(chatId).get();
                        us.setStageOfUs(String.valueOf(Stage.EnterFalseAnswerToAdd));
                        userRepository.save(us);
                        List<List<Button>> buttons = new ArrayList<>();
                        List<Button> row = new ArrayList<>();
                        row.add(new Button("Закончить с вопросом", "end"));
                        buttons.add(row);
                        SendMessageWithKeyboard(chatId, "Введи неправильный ответ.", buttons);
                        break;
                    } else if (user.getStageOfUs().equals(Stage.EnterFalseAnswerToAdd.toString()) && !messageText.startsWith("/")) {
                        Answer answer = new Answer();
                        answer.setAnswer(messageText);
                        answer.setQuestion(enterQuestion.get(chatId));
                        answer.setIsRight(false);
                        answerRepository.save(answer);
                        var us = userRepository.findById(chatId).get();
                        us.setStageOfUs(String.valueOf(Stage.EnterFalseAnswerToAdd));
                        userRepository.save(us);
                        List<List<Button>> buttons = new ArrayList<>();
                        List<Button> row = new ArrayList<>();
                        row.add(new Button("Закончить с вопросом", "end"));
                        buttons.add(row);
                        SendMessageWithKeyboard(chatId, "Введи неправильный ответ.", buttons);
                        break;
                    } else if (user.getStageOfUs().equals(Stage.ChangeRussTextAbout.toString()) && !messageText.startsWith("/")) {
                        var text = textOfMessageRepository.findByType("about").get(0);
                        text.setTextInRussian(EmojiParser.parseToUnicode(messageText));
                        textOfMessageRepository.save(text);
                        ChangeToDoingNothing(chatId);
                        break;
                    } else if (user.getStageOfUs().equals(Stage.ChangeTajikTextAbout.toString()) && !messageText.startsWith("/")) {
                        var text = textOfMessageRepository.findByType("about").get(0);
                        text.setTextInTajik(messageText);
                        textOfMessageRepository.save(text);
                        ChangeToDoingNothing(chatId);
                        break;
                    } else if (user.getStageOfUs().equals(Stage.EnterRussTextAbout.toString()) && !messageText.startsWith("/")) {
                        TextOfMessage text = new TextOfMessage();
                        text.setType("about");
                        text.setTextInRussian(messageText);
                        text.setTextInTajik("setTextInTajik");
                        textOfMessageRepository.save(text);
                        ChangeToDoingNothing(chatId);
                        break;
                    } else if (user.getStageOfUs().equals(Stage.EnterTajikTextAbout.toString()) && !messageText.startsWith("/")) {
                        TextOfMessage text = new TextOfMessage();
                        text.setType("about");
                        text.setTextInTajik(messageText);
                        text.setTextInRussian("setTextInRussian");
                        textOfMessageRepository.save(text);
                        ChangeToDoingNothing(chatId);
                        break;
                    } else if (user.getStageOfUs().equals(Stage.EnterRussTextSupport.toString()) && !messageText.startsWith("/")) {
                        TextOfMessage text = new TextOfMessage();
                        text.setType("support");
                        text.setTextInRussian(messageText);
                        text.setTextInTajik("setTextInTajik");
                        textOfMessageRepository.save(text);
                        ChangeToDoingNothing(chatId);
                        break;
                    } else if (user.getStageOfUs().equals(Stage.EnterTajikTextSupport.toString()) && !messageText.startsWith("/")) {
                        TextOfMessage text = new TextOfMessage();
                        text.setType("support");
                        text.setTextInTajik(messageText);
                        text.setTextInRussian("setTextInRussian");
                        textOfMessageRepository.save(text);
                        ChangeToDoingNothing(chatId);
                        break;
                    } else if (user.getStageOfUs().equals(Stage.ChangeTextSupport.toString()) && !messageText.startsWith("/")) {
                        var text = textOfMessageRepository.findByType("support").get(0);
                        text.setType(messageText);
                        textOfMessageRepository.save(text);
                        ChangeToDoingNothing(chatId);
                        break;
                    } else if (user.getStageOfUs().equals(Stage.ChangeRussTextSupport.toString()) && !messageText.startsWith("/")) {
                        var text = textOfMessageRepository.findByType("support").get(0);
                        text.setTextInRussian(EmojiParser.parseToUnicode(messageText));
                        textOfMessageRepository.save(text);
                        ChangeToDoingNothing(chatId);
                        break;
                    } else if (user.getStageOfUs().equals(Stage.ChangeTajikTextSupport.toString()) && !messageText.startsWith("/")) {
                        var text = textOfMessageRepository.findByType("support").get(0);
                        text.setTextInTajik(messageText);
                        textOfMessageRepository.save(text);
                        ChangeToDoingNothing(chatId);
                        break;
                    }
                    sendMessage(chatId, "Извините, команда не распознана.");
                    break;
            }
        }
    }
    private void ChangeToDoingNothing(long chatId) {
        User u = userRepository.findById(chatId).get();
        u.setStageOfUs(String.valueOf(Stage.DoingNothing));
        userRepository.save(u);
    }
    private void getCallBackQueryUpdateFromAdmin(Update update) {
        String callbackData = update.getCallbackQuery().getData();
        var m = update.getCallbackQuery().getMessage();
        Integer messageId = update.getCallbackQuery().getMessage().getMessageId();
        long chatId = update.getCallbackQuery().getMessage().getChatId();

        if (callbackData.equals("startTest")) {
            startTest(chatId);
            return;
        } else if (callbackData.equals("end")) {
            enterQuestion.put(chatId, null);
            sendMessage(chatId, "Вы закончили оформлять вопрос.");
            return;
        } else if (callbackData.equals("changeAbout")) {
            changeAbout(chatId);
        } else if (callbackData.equals("changeSupport")) {
            changeSupport(chatId);
        } else if (callbackData.equals("changeRussTextAbout")) {
            changeRussTextAbout(chatId);
        } else if (callbackData.equals("changeTajikTextAbout")) {
            changeTajikTextAbout(chatId);
        } else if (callbackData.equals("changePhotoAbout")) {
            changePhotoAbout(chatId);
        } else if (callbackData.equals("changeRussSupport")) {
            changeRussTextSupport(chatId);
        } else if (callbackData.equals("changeTajikSupport")) {
            changeTajikTextSupport(chatId);
        } else if (callbackData.equals("langRuss")) {
            setLanguage(chatId, Language.RUS);
        } else if (callbackData.equals("langTajik")) {
            setLanguage(chatId, Language.TAJIK);
        } else if (callbackData.equals("changeTajikAbout")) {
            changeTajikAbout(chatId);
        } else if (callbackData.equals("changeRussAbout")) {
            changeRussAbout(chatId);
        }
        if (callbackData.startsWith("answer_")) {
            var str = callbackData.split("_");
            long id = Long.parseLong(str[1]);
            UserTestSession testSession = activeSessions.get(chatId);
            testService.processUserAnswer(chatId, testSession, id); // Передаем сессию

            askCurrentQuestion(chatId, testSession, messageId);
        }
    }
    private boolean isUserSubscribed(long userId) {
        GetChatMember getChatMember = new GetChatMember();
        getChatMember.setChatId(CHANNELNAME);
        getChatMember.setUserId(userId);

        try {
            ChatMember chatMember = execute(getChatMember);
            String status = chatMember.getStatus();

            return status.equals("member") || status.equals("administrator") || status.equals("creator");
        } catch (TelegramApiException e) {
            e.printStackTrace();
            return false;
        }
    }
    private void changeCurrentLanguage(long chatId) {
        var lang = currentLanguageRepository.findByUser(userRepository.findById(chatId).get());
        if (lang.getLanguage().equals(String.valueOf(Language.RUS))) {
            lang.setLanguage(String.valueOf(Language.TAJIK));
        } else {
            lang.setLanguage(String.valueOf(Language.RUS));
        }
        currentLanguageRepository.save(lang);
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(String.valueOf(chatId));
        sendMessage.setText("Language was changed");
        if (userRepository.findById(chatId).get().getRole().equals(Role.Admin)) {
            sendMessage.setReplyMarkup(KeyboardMarkupBuilder.setReplyKeyboardWithRaw(getKeyboardForAdmin(chatId)));
        } else {
            sendMessage.setReplyMarkup(KeyboardMarkupBuilder.setReplyKeyboardWithRaw(getKeyboardForCustomer(chatId)));
        }

        try {
            execute(sendMessage);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }
    private void photoProcessing(Update update) {
        long chatId = update.getMessage().getChatId();
        User user = userRepository.findById(chatId).get();
        if (user.getRole().equals(Role.Admin)) {
            var photos = update.getMessage().getPhoto();

            PhotoSize largestPhoto = photos.stream()
                    .max(Comparator.comparingInt(PhotoSize::getFileSize))
                    .orElse(null);
            if (user.getStageOfUs().equals(Stage.ChangePhotoAbout.toString())) {
                if (largestPhoto != null) {
                    String fileId = largestPhoto.getFileId();
                    try {
                        byte[] fileData = downloadFileAsBytes(fileId);
                        Image image = new Image();
                        image.setImg(fileData);
                        image.setTitle("about");
                        imageRepository.save(image);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }
    private void processFile(Update update) {
        Message message = update.getMessage();
        String fileId = message.getDocument().getFileId();
        User user = userRepository.findById(message.getChatId()).get();
        if (user.getRole().equals(Role.Admin)) {
            GetFile getFileMethod = new GetFile();
            getFileMethod.setFileId(fileId);

            try {
                File file = execute(getFileMethod);
                java.io.File downloadedFile = downloadFile(file);

                if (user.getStageOfUs().equals(String.valueOf(Stage.EnterListOfQuestions))) {
                    processQuestionsFile(downloadedFile);
                } else if (user.getStageOfUs().equals(String.valueOf(Stage.EnterListOfWords))) {
                    processWordsFile(downloadedFile);
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
    public void processWordsFile(java.io.File file) {
        try (FileInputStream fis = new FileInputStream(file)) {
            Workbook workbook = WorkbookFactory.create(fis);
            Sheet sheet = workbook.getSheetAt(0);

            boolean isFirst = true;
            for (Row row : sheet) {
                if (isFirst) {
                    isFirst = false;
                    continue;
                }
                int i = 0;
                Word word = new Word();
                for (Cell cell : row) {
                    if (i == 0) {
                        word.setWordInEnglish(cell.getStringCellValue());
                    } else if (i == 1) {
                        word.setTranscription(cell.getStringCellValue());
                    } else if (i == 2) {
                        word.setWordInTajik(cell.getStringCellValue());
                    } else if (i == 3) {
                        word.setWordInRussian(cell.getStringCellValue());
                        word.setCreatedAt(LocalDateTime.now());
                        wordRepository.save(word);
                    }
                    i++;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public void processQuestionsFile(java.io.File file) {
        List<Question> questions = (List<Question>) questionRepository.findAll();
        try (FileInputStream fis = new FileInputStream(file)) {
            Workbook workbook = WorkbookFactory.create(fis);
            Sheet sheet = workbook.getSheetAt(0);

            boolean isFirst = true;
            for (Row row : sheet) {
                if (isFirst) {
                    isFirst = false;
                    continue;
                }
                int i = 0;
                Question question = new Question();
                Question doneQuestion = new Question();
                for (Cell cell : row) {
                    if (i == 0) {
                        int l = 1;
                        for (var q:
                             questions) {
                            if (q.getText().equals(cell.getStringCellValue())) {
                                l = 0;
                                break;
                            }
                        }
                        if (l == 0) {
                            break;
                        }
                        question.setText(cell.getStringCellValue());
                    } else if (i == 1) {
                        var st = cell.getNumericCellValue();
                        question.setDifficulty((int) st);
                        question.setCreatedAt(LocalDateTime.now());
                        doneQuestion = questionRepository.save(question);
                    } else if (i == 2) {
                        if (doneQuestion == null) {
                            throw new RuntimeException("question is not exists");
                        }
                        Answer answer = new Answer();
                        answer.setAnswer(cell.getStringCellValue());
                        answer.setQuestion(doneQuestion);
                        answer.setIsRight(true);
                        answerRepository.save(answer);
                    } else if (i > 2) {
                        if (doneQuestion == null) {
                            throw new RuntimeException("question is not exists");
                        }
                        Answer answer = new Answer();
                        answer.setAnswer(cell.getStringCellValue());
                        answer.setQuestion(doneQuestion);
                        answer.setIsRight(false);
                        answerRepository.save(answer);
                    }
                    i++;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    private void change(long chatId) {
        List<List<Button>> buttons = new ArrayList<>();
        List<Button> row1 = new ArrayList<>();
        row1.add(new Button("Обо мне", "changeAbout"));
        buttons.add(row1);
        List<Button> row2 = new ArrayList<>();
        row2.add(new Button("Поддержка", "changeSupport"));
        buttons.add(row2);
        SendMessageWithKeyboard(chatId, "Что нужно изменить?", buttons);
    }
    private void changeAbout(long chatId) {
        List<List<Button>> buttons = new ArrayList<>();
        List<Button> row1 = new ArrayList<>();
        row1.add(new Button("Русский", "changeRussAbout"));
        buttons.add(row1);
        List<Button> row2 = new ArrayList<>();
        row2.add(new Button("Таджикский", "changeTajikAbout"));
        buttons.add(row2);
        SendMessageWithKeyboard(chatId, "На каком языке изменить раздел Обо мне?", buttons);
    }
    private void changeRussAbout(long chatId) {
        List<List<Button>> buttons = new ArrayList<>();
        List<Button> row1 = new ArrayList<>();
        row1.add(new Button("Текст", "changeRussTextAbout"));
        buttons.add(row1);
        List<Button> row2 = new ArrayList<>();
        row2.add(new Button("Фотографию", "changePhotoAbout"));
        buttons.add(row2);
        SendMessageWithKeyboard(chatId, "Что нужно изменить в разделе Обо мне?", buttons);
    }
    private void changeTajikAbout(long chatId) {
        List<List<Button>> buttons = new ArrayList<>();
        List<Button> row1 = new ArrayList<>();
        row1.add(new Button("Текст", "changeTajikTextAbout"));
        buttons.add(row1);
        List<Button> row2 = new ArrayList<>();
        row2.add(new Button("Фотографию", "changePhotoAbout"));
        buttons.add(row2);
        SendMessageWithKeyboard(chatId, "Что нужно изменить в разделе Обо мне?", buttons);
    }
    private void changeSupport(long chatId) {
        List<List<Button>> buttons = new ArrayList<>();
        List<Button> row1 = new ArrayList<>();
        row1.add(new Button("Русский", "changeRussSupport"));
        buttons.add(row1);
        List<Button> row2 = new ArrayList<>();
        row2.add(new Button("Таджикский", "changeTajikSupport"));
        buttons.add(row2);
        SendMessageWithKeyboard(chatId, "На каком языке изменить раздел Поддержки?", buttons);
    }
    private void changePhotoAbout(long chatId) {
        String text = "Введи новую фотографию для раздела Обо мне.";
        User user = userRepository.findById(chatId).get();
        user.setStageOfUs(String.valueOf(Stage.ChangePhotoAbout));
        userRepository.save(user);
        SendMessage(chatId, text);
    }
    private void changeRussTextAbout(long chatId) {
        String about = "";
        String text = "Введи новую версию текста раздела Обо мне.";
        try {
            about = textOfMessageRepository.findByType("about").get(0).getTextInRussian();
            text += "А пока вот тебе предыдущая версия:\n" + about;
        } catch (Exception e) {
            User user = userRepository.findById(chatId).get();
            user.setStageOfUs(String.valueOf(Stage.EnterRussTextAbout));
            userRepository.save(user);
            SendMessage(chatId, text);
            return;
        }

        User user = userRepository.findById(chatId).get();
        user.setStageOfUs(String.valueOf(Stage.ChangeRussTextAbout));
        userRepository.save(user);
        SendMessage(chatId, text);
    }
    private void changeTajikTextAbout(long chatId) {
        String about = "";
        String text = "Введи новую версию текста раздела Обо мне.";
        try {
            about = textOfMessageRepository.findByType("about").get(0).getTextInTajik();
            text += "А пока вот тебе предыдущая версия:\n" + about;
        } catch (Exception e) {
            User user = userRepository.findById(chatId).get();
            user.setStageOfUs(String.valueOf(Stage.EnterTajikTextAbout));
            userRepository.save(user);
            SendMessage(chatId, text);
            return;
        }

        User user = userRepository.findById(chatId).get();
        user.setStageOfUs(String.valueOf(Stage.ChangeTajikTextAbout));
        userRepository.save(user);
        SendMessage(chatId, text);
    }
    private void changeRussTextSupport(long chatId) {
        String about = "";
        String text = "Введи новую версию текста раздела Поддержка.";
        try {
            about = textOfMessageRepository.findByType("support").get(0).getTextInRussian();
            text += "А пока вот тебе предыдущая версия:\n" + about;
        } catch (Exception e) {
            User user = userRepository.findById(chatId).get();
            user.setStageOfUs(String.valueOf(Stage.EnterRussTextSupport));
            userRepository.save(user);
            SendMessage(chatId, text);
            return;
        }

        User user = userRepository.findById(chatId).get();
        user.setStageOfUs(String.valueOf(Stage.ChangeRussTextSupport));
        userRepository.save(user);
        SendMessage(chatId, text);
    }
    private void changeTajikTextSupport(long chatId) {
        String about = "";
        String text = "Введи новую версию текста раздела Поддержка.";
        try {
            about = textOfMessageRepository.findByType("support").get(0).getTextInTajik();
            text += "А пока вот тебе предыдущая версия:\n" + about;
        } catch (Exception e) {
            User user = userRepository.findById(chatId).get();
            user.setStageOfUs(String.valueOf(Stage.EnterTajikTextSupport));
            userRepository.save(user);
            SendMessage(chatId, text);
            return;
        }

        User user = userRepository.findById(chatId).get();
        user.setStageOfUs(String.valueOf(Stage.ChangeTajikTextSupport));
        userRepository.save(user);
        SendMessage(chatId, text);
    }
    private void startFunc(Update update, long chatId) {
        registerUser(update.getMessage());
        var chat = update.getMessage().getChat();
        List<List<Button>> buttons = new ArrayList<>();
        List<Button> row = new ArrayList<>();
        row.add(new Button("Начать тестирование", "startTest"));
        buttons.add(row);
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(String.valueOf(chatId));
        sendMessage.setText(EmojiParser.parseToUnicode("Привет, " + chat.getFirstName() + "! " +
                "Я - бот, который определит твой уровень англ."));
        if (userRepository.findById(chatId).get().getRole().equals(Role.Customer)) {
            sendMessage.setReplyMarkup(KeyboardMarkupBuilder.setReplyKeyboardWithRaw(getKeyboardForCustomer(chatId)));
        } else {
            sendMessage.setReplyMarkup(KeyboardMarkupBuilder.setReplyKeyboardWithRaw(getKeyboardForAdmin(chatId)));
        }

        try {
            execute(sendMessage);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }

        SendMessageWithKeyboard(chatId, EmojiParser.parseToUnicode("Чтобы начать тест нажми кнопку " +
                "\"Начать тестирование\""), buttons);
    }
    private void startTest(long chatId) {
        UserTestSession testSession = testService.startTest(chatId);
        activeSessions.put(chatId, testSession);
        askCurrentQuestion(chatId, testSession, 0);
    }
    private void askCurrentQuestion(Long chatId, UserTestSession testSession, Integer messageId) {
        Question currentQuestion = testService.getNextQuestion(testSession);
        if (currentQuestion != null) {
            int numOfQuestion = testSession.getUserAnswers().size() + 1;
            String questionText = numOfQuestion + "/" + countOfQuestions + " " +currentQuestion.getText() + "\n";
            var ans = answerRepository.findAllByQuestion(currentQuestion);
            List<List<Button>> buttons = new ArrayList<>(ans.size());
            Random random = new Random();
            int numberOfRightAnswer = random.nextInt(ans.size());
            for (int i = 0; i < ans.size(); i++) {
                if (i < numberOfRightAnswer) {
                    List<Button> row = new ArrayList<>();
                    Answer a = ans.get(i + 1);
                    row.add(new Button(a.getAnswer(), "answer_" + a.getId()));
                    buttons.add(row);
                } else if (i > numberOfRightAnswer) {
                    List<Button> row = new ArrayList<>();
                    Answer a = ans.get(i);
                    row.add(new Button(a.getAnswer(), "answer_" + a.getId()));
                    buttons.add(row);
                } else {
                    List<Button> row = new ArrayList<>();
                    Answer a = ans.get(0);
                    row.add(new Button(a.getAnswer(), "answer_" + a.getId()));
                    buttons.add(row);
                }
            }
            if (messageId == 0) {
                SendMessageWithKeyboard(chatId, questionText, buttons);
            } else {
                EditMessageText editMessageText = new EditMessageText();
                editMessageText.setMessageId(messageId);
                editMessageText.setChatId(String.valueOf(chatId));
                editMessageText.setText(questionText);

                var markup = KeyboardMarkupBuilder.setKeyboardWithRaw(buttons);
                editMessageText.setParseMode("HTML");
                editMessageText.setDisableWebPagePreview(true);
                editMessageText.setReplyMarkup(markup);

                try {
                    execute(editMessageText);
                } catch (TelegramApiException e) {
                    e.printStackTrace();
                }
            }
        } else {
            int countRight = 0;
            var ans = testSession.getUserAnswers();
            for (var a:
                 ans) {
                if (a.getIsCorrect()) {
                    countRight++;
                }
            }
            printResults(chatId, countRight, messageId);
            activeSessions.remove(chatId);
        }
    }
    private void printResults(long chatId, int countOfRight, Integer messageId) {
        var lang = currentLanguageRepository.findByUser(userRepository.findById(chatId).get());
        String result = "";
        if (countOfRight >= 0 && countOfRight <= 5) {
            result = "Beginner (A1) \n";
            result += lang.getLanguage().equals(String.valueOf(Language.RUS)) ? BeginnerRuss : BeginnerTajik;
        } else if (countOfRight >= 6 && countOfRight <= 10) {
            result = "Elementary (A2) \n";
            result += lang.getLanguage().equals(String.valueOf(Language.RUS)) ? ElementaryRuss : ElementaryTajik;
        } else if (countOfRight >= 11 && countOfRight <= 15) {
            result = "Pre-Intermediate (B1) \n";
            result += lang.getLanguage().equals(String.valueOf(Language.RUS)) ? PreIntermediateRuss : PreIntermediateTajik;
        } else if (countOfRight >= 16 && countOfRight <= 20) {
            result = "Intermediate (B2) \n";
            result += lang.getLanguage().equals(String.valueOf(Language.RUS)) ? IntermediateRuss : IntermediateTajik;
        } else if (countOfRight >= 21 && countOfRight <= 23) {
            result = "Upper-Intermediate (C1) \n";
            result += lang.getLanguage().equals(String.valueOf(Language.RUS)) ? UpperIntermediateRuss : UpperIntermediateTajik;
        } else if (countOfRight >= 24 && countOfRight <= 25) {
            result = "Advanced (C2) \n";
            result += lang.getLanguage().equals(String.valueOf(Language.RUS)) ? AdvancedRuss : AdvancedTajik;
        }
        EditMessageText editMessageText = new EditMessageText();
        editMessageText.setMessageId(messageId);
        editMessageText.setChatId(String.valueOf(chatId));
        editMessageText.setText(result);

        editMessageText.setParseMode("HTML");
        editMessageText.setDisableWebPagePreview(true);

        try {
            execute(editMessageText);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }
    private void getReport(long chatId) {
        try {
            byte[] excelData = createExcelReport();

            SendDocument sendDocument = new SendDocument();
            sendDocument.setChatId(String.valueOf(chatId));
            sendDocument.setDocument(new InputFile(new ByteArrayInputStream(excelData), "report.xlsx"));
            execute(sendDocument);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }
    public byte[] createExcelReport() {
        Workbook workbook = new XSSFWorkbook();
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

        createQuestionsList(workbook);
        createWordsList(workbook);

        try {
            workbook.write(outputStream);
            System.out.println("Отчет успешно создан: ");
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        } finally {
            try {
                workbook.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return outputStream.toByteArray();
    }
    private void createQuestionsList(Workbook workbook) {
        Sheet sheet4 = workbook.createSheet("Вопросы");
        createQuestionHeader(sheet4);
        List<Question> questions = (List<Question>) questionRepository.findAll();
        int j = 1;
        for (var q:
                questions) {
            Row row = sheet4.createRow(j);
            j++;
            row.createCell(0).setCellValue(q.getId());
            row.createCell(1).setCellValue(q.getText());
            row.createCell(2).setCellValue(q.getDifficulty());
            List<Answer> ans = answerRepository.findAllByQuestion(q);
            for (int i = 0; i < ans.size(); i++) {
                if (i == 0) {
                    row.createCell(3).setCellValue(ans.get(i).getAnswer());
                    row.createCell(4).setCellValue(ans.get(i).getIsRight());
                } else {
                    Row row1 = sheet4.createRow(j);
                    j++;
                    row1.createCell(3).setCellValue(ans.get(i).getAnswer());
                    row1.createCell(4).setCellValue(ans.get(i).getIsRight());
                }
            }
        }
    }
    private void createQuestionHeader(Sheet sheet) {
        Row headerRow = sheet.createRow(0);
        headerRow.createCell(0).setCellValue("Id вопроса");
        headerRow.createCell(1).setCellValue("Вопрос");
        headerRow.createCell(2).setCellValue("Сложность вопроса");
        headerRow.createCell(3).setCellValue("Ответ");
        headerRow.createCell(4).setCellValue("Правильность ответа");
    }
    private void createWordsList(Workbook workbook) {
        Sheet sheet4 = workbook.createSheet("Слова");
        createWordHeader(sheet4);
        List<Word> words = (List<Word>) wordRepository.findAll();
        System.out.println(words.size());
        int j = 1;
        for (var w:
                words) {
            Row row = sheet4.createRow(j);
            j++;
            row.createCell(0).setCellValue(w.getId());
            row.createCell(1).setCellValue(w.getWordInEnglish());
            row.createCell(2).setCellValue(w.getWordInRussian());
            row.createCell(3).setCellValue(w.getWordInTajik());
        }
    }
    private void createWordHeader(Sheet sheet) {
        Row headerRow = sheet.createRow(0);
        headerRow.createCell(0).setCellValue("Id слова");
        headerRow.createCell(1).setCellValue("Слово на англ");
        headerRow.createCell(2).setCellValue("Слово на рус");
        headerRow.createCell(3).setCellValue("Слово на тадж");
    }
    private void onboard(long chatId) {
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(String.valueOf(chatId));
        sendMessage.setText("Введи username пользователя в телеграмме, но до этого пользователь должен нажать /start");
        var us = userRepository.findById(chatId).get();
        us.setStageOfUs(String.valueOf(Stage.EnterNewAdminUser));
        userRepository.save(us);
        try {
            execute(sendMessage);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }
    private void about(long chatId) {
        List<Image> im = (List<Image>) imageRepository.findAll();
        var text = textOfMessageRepository.findByType("about").get(0);
        String sendText = "";
        var lang = currentLanguageRepository.findByUser(userRepository.findById(chatId).get());
        if (lang == null || lang.getLanguage().equals(String.valueOf(Language.RUS))) {
            sendText = text.getTextInRussian();
        } else if (lang.getLanguage().equals(String.valueOf(Language.TAJIK))) {
            sendText = text.getTextInTajik();
        }

        byte[] imageData = im.get(im.size() - 1).getImg();
        InputStream imageStream = new ByteArrayInputStream(imageData);
        SendPhoto sendPhoto = new SendPhoto();
        sendPhoto.setChatId(String.valueOf(chatId));
        sendPhoto.setPhoto(new InputFile(imageStream, "image.jpg"));
        sendPhoto.setCaption(sendText);

        try {
            execute(sendPhoto);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }
    private void support(long chatId) {
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(String.valueOf(chatId));
        var text = textOfMessageRepository.findByType("support").get(0);
        String sendText = "";
        var lang = currentLanguageRepository.findByUser(userRepository.findById(chatId).get());
        if (lang == null || lang.getLanguage().equals(String.valueOf(Language.RUS))) {
            sendText = text.getTextInRussian();
        } else if (lang.getLanguage().equals(String.valueOf(Language.TAJIK))) {
            sendText = text.getTextInTajik();
        }
        sendMessage.setText(sendText);

        try {
            execute(sendMessage);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }
    private void addQuestion(long chatId) {
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(String.valueOf(chatId));
        sendMessage.setText("Введи вопрос:");
        var user = userRepository.findById(chatId).get();
        user.setStageOfUs(String.valueOf(Stage.EnterQuestionToAdd));
        userRepository.save(user);
        try {
            execute(sendMessage);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }
    private void addQuestions(long chatId) {
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(String.valueOf(chatId));
        sendMessage.setText("Отправь файл:");
        var user = userRepository.findById(chatId).get();
        user.setStageOfUs(String.valueOf(Stage.EnterListOfQuestions));
        userRepository.save(user);
        try {
            execute(sendMessage);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }
    private void addWords(long chatId) {
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(String.valueOf(chatId));
        sendMessage.setText("Отправь файл со словами:");
        var user = userRepository.findById(chatId).get();
        user.setStageOfUs(String.valueOf(Stage.EnterListOfWords));
        userRepository.save(user);
        try {
            execute(sendMessage);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }
    public void SendMessage(long chatId, String text) {
        SendMessage message = new SendMessage();
        message.setChatId(String.valueOf(chatId));
        message.setText(text);
        message.setParseMode("HTML");
        message.setDisableWebPagePreview(true);
        try {
            execute(message);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }
    public void SendMessageWithKeyboard(long chatId, String text, List<List<Button>> buttons) {
        SendMessage message = new SendMessage();
        message.setChatId(String.valueOf(chatId));
        message.setText(text);
        var markup = KeyboardMarkupBuilder.setKeyboardWithRaw(buttons);
        message.setParseMode("HTML");
        message.setDisableWebPagePreview(true);
        message.setReplyMarkup(markup);
        try {
            execute(message);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }
    private byte[] downloadFileAsBytes(String fileId) throws TelegramApiException {
        GetFile getFile = new GetFile(fileId);
        File file = execute(getFile);
        String fileUrl = "https://api.telegram.org/file/bot" + getBotToken() + "/" + file.getFilePath();

        try (InputStream inputStream = new URL(fileUrl).openStream()) {
            return inputStream.readAllBytes();
        } catch (Exception e) {
            e.printStackTrace();
            return new byte[0];
        }
    }

    //todo updateinfo
    /*private void updateInfo(long chatId) {
        if (userRepository.findById(chatId).isPresent()) {
            currentInds.computeIfAbsent(chatId, k -> 0);
            currentProdResInReview.computeIfAbsent(chatId, k -> new ArrayList<>());
            currentProdResInOrder.computeIfAbsent(chatId, k -> new ArrayList<>());
            curProdToR.computeIfAbsent(chatId, k -> new ArrayList<>());
            hasInvited.computeIfAbsent(chatId, k -> true);
        }
    }*/
    private List<List<Button>> getKeyboardForCustomer(long chatId) {
        List<List<Button>> buttons = new ArrayList<>();
        var lang = currentLanguageRepository.findByUser(userRepository.findById(chatId).get());
        boolean isRus = lang.getLanguage().equals(String.valueOf(Language.RUS));
        String start = isRus ? "Начать тест \uD83D\uDCDD"  :  "Оғози тест \uD83D\uDCDD";
        String about = isRus ? "Обо мне \uD83D\uDCD6"  :  "Дар бораи ман \uD83D\uDCD6";
        String support = isRus ? "Поддержка \uD83D\uDCAC"  :  "Тамос гирифтан \uD83D\uDCAC";
        String changeLang = isRus ? "Тоҷикӣ \uD83C\uDDF9\uD83C\uDDEF"  :  "Русский \uD83C\uDDF7\uD83C\uDDFA";

        List<Button> row1 = new ArrayList<>();
        row1.add(new Button(start, ""));
        buttons.add(row1);

        List<Button> row2 = new ArrayList<>();
        row2.add(new Button(about, ""));
        row2.add(new Button(support, ""));
        buttons.add(row2);

        List<Button> row3 = new ArrayList<>();
        row3.add(new Button(changeLang, ""));
        buttons.add(row3);
        return buttons;
    }
    private List<List<Button>> getKeyboardForAdmin(long chatId) {
        List<List<Button>> buttons = new ArrayList<>();
        var lang = currentLanguageRepository.findByUser(userRepository.findById(chatId).get());
        boolean isRus = lang.getLanguage().equals(String.valueOf(Language.RUS));
        String start = isRus ? "Начать тест \uD83D\uDCDD"  :  "Оғози тест \uD83D\uDCDD";
        String about = isRus ? "Обо мне \uD83D\uDCD6"  :  "Дар бораи ман \uD83D\uDCD6";
        String support = isRus ? "Поддержка \uD83D\uDCAC"  :  "Тамос гирифтан \uD83D\uDCAC";
        String changeLang = isRus ? "Тоҷикӣ \uD83C\uDDF9\uD83C\uDDEF"  :  "Русский \uD83C\uDDF7\uD83C\uDDFA";
        String change = isRus ? "Редактировать"  :  "Ред";
        String addQuestions = isRus ? "Добавить вопросы"  :  "Вопросы";
        String addWords = isRus ? "Добавить слова"  :  "Слова";
        String report = isRus ? "Отчет по боту"  :  "Отчет";

        List<Button> row1 = new ArrayList<>();
        row1.add(new Button(start, ""));
        buttons.add(row1);

        List<Button> row2 = new ArrayList<>();
        row2.add(new Button(about, ""));
        row2.add(new Button(support, ""));
        buttons.add(row2);

        List<Button> row3 = new ArrayList<>();
        row3.add(new Button(change, ""));
        row3.add(new Button(changeLang, ""));
        buttons.add(row3);

        List<Button> row4 = new ArrayList<>();
        row4.add(new Button(addQuestions, ""));
        row4.add(new Button(report, ""));
        row4.add(new Button(addWords, ""));
        buttons.add(row4);
        return buttons;
    }
    public void registerUser(Message msg) {
        if (userRepository.findById(msg.getChatId()).isEmpty()) {

            var chatId = msg.getChatId();
            var chat = msg.getChat();

            User user = new User();
            user.setChatId(chatId);
            user.setName(chat.getFirstName());
            user.setUserName(chat.getUserName());
            user.setStageOfUs(Stage.EnterFirstName.toString());
            if (chatId == 959316826L) {
                user.setRole(Role.Admin);
            } else {
                user.setRole(Role.Customer);
            }

            var u = userRepository.save(user);

            //setCurrentLanguageToUser(chatId);
            CurrentLanguage currentLanguage = new CurrentLanguage();
            currentLanguage.setUser(u);
            currentLanguage.setLanguage(String.valueOf(Language.RUS));
            currentLanguageRepository.save(currentLanguage);
            setUserCommands(chatId);
        } else {
            var chatId = msg.getChatId();
            var chat = msg.getChat();
            if (chat.getUserName() != null) {
                User u = userRepository.findById(chatId).get();
                u.setUserName(chat.getUserName());
                userRepository.save(u);
            }
            setUserCommands(chatId);
        }
    }
    private void setLanguage(long chatId, Language language) {
        CurrentLanguage currentLanguage = new CurrentLanguage();
        currentLanguage.setUser(userRepository.findById(chatId).get());
        currentLanguage.setLanguage(String.valueOf(language));
        currentLanguageRepository.save(currentLanguage);
        sendMessage(chatId, "Язык установлен.");
    }
    private void setUserCommands(long chatId) {
        List<BotCommand> listOfCommands = new ArrayList<>();
        User user = userRepository.findById(chatId).get();

        if (user.getRole().equals(Role.Customer)) {
            listOfCommands.add(new BotCommand("/start", "Начать тест"));
            listOfCommands.add(new BotCommand("/about", "Обо мне"));
            listOfCommands.add(new BotCommand("/support", "Поддержка"));
        } else {
            listOfCommands.add(new BotCommand("/start", "Начать тест"));
            listOfCommands.add(new BotCommand("/about", "Обо мне"));
            listOfCommands.add(new BotCommand("/support", "Поддержка"));
        }

        try {
            SetMyCommands setMyCommands = new SetMyCommands();
            setMyCommands.setCommands(listOfCommands);
            this.execute(setMyCommands);
        } catch (TelegramApiException e) {
            System.out.println(e.getMessage());
        }

    }
    public void sendMessage(long chatId, String textToSend) {
        SendMessage message = new SendMessage();
        message.setChatId(String.valueOf(chatId));
        message.setText(textToSend);
        message.enableHtml(true);
        message.setDisableWebPagePreview(true);

        try {
            execute(message);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }
    @Override
    public String getBotUsername() {
        return this.config.getBotName();
    }

    @Override
    public String getBotToken() {
        return this.config.getToken();
    }
}
