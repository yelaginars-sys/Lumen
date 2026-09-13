# AGENTS.md — контекст проекта Lumen для ИИ-ассистентов

Привет, коллега-ИИ. Это рабочий контекст проекта. Прочитай полностью перед любой задачей — тут всё, что нужно, чтобы работать так же, как ассистент, который вёл этот проект до тебя (ZCode / GLM).

## Что за проект

- **Lumen** — utility-клиент (чит-клиент) для Minecraft **1.21.4 / Fabric 0.18.4**, восстановленный из декомпиляции `lumen-kvm.jar` (516 java-файлов, часть имён вида `recoveredField*`/`helper*`/`volume*` — это артефакты декомпиляции, НЕ переименовывай их массово).
- Рабочая папка: `C:\Users\Админ\Desktop\Lumen-master`
- GitHub: `https://github.com/yelaginars-sys/Lumen`
- Второй проект-донор: `C:\Users\Админ\Desktop\arbuz` (клиент «Arbuz 1.21.4», НЕ git-репозиторий, Loom 1.10.5). Оттуда портируем фичи в Lumen.

## Железезные правила Git (не нарушать)

1. **Коммиты всегда от `cert13337`** — уже настроено локально в репо (`git config user.name/user.email`), не меняй и не задавай глобально.
2. **Работаем только в ветке `cert-dev`** — это личная ветка владельца (Cert). Она уже checkout и трекает `origin/cert-dev`.
3. **НЕ пушить в `master`** — там работает второй разработчик (`yelaginars-sys`), master защищён от force-push. Он сам мержит cert-dev, когда считает нужным.
4. **Перед пушем всегда**: `git pull --rebase origin cert-dev` → `git push`. Напарник пушит по несколько раз в час, без rebase пуш будет отбиваться («Note about fast-forwards»).
5. Стиль коммитов — как в истории (`git log --oneline`): кратко, по-английски, в формате `Область: что сделал`. Примеры: `Velocity: add New Grim mode (cancel + PosRot/StopDestroy spoof on tick)`, `Main menu: smaller cards, account switcher button replaces nick input`.
6. Обычный цикл задачи: правки кода → `gradlew.bat compileJava` → commit → pull --rebase → push.

## Сборка и запуск

- `gradlew.bat compileJava` — быстрая проверка кода (делай после КАЖДОГО изменения кода).
- `gradlew.bat build` — полная сборка.
- `gradlew.bat runClient` — запуск игры. Запускай в фоне (`Start-Process`/background), игра открывается ~60–90 сек.
- Уже настроено на машине (НЕ трогать): JDK 21 в `C:\Users\Админ\.jdks\corretto-21.0.12.1`; в `~/.gradle/gradle.properties` прописано `org.gradle.java.home=C:/Users/7272~1/.jdks/corretto-21.0.12.1` (короткий путь, т.к. Properties не понимают кириллицу); wrapper на **Gradle 8.12.1**, `networkTimeout=120000`.
- **НЕ обновляй Gradle до 9.x и не поднимай major-версию Loom** — Loom 1.9.2 несовместим с Gradle 9, при запуске игры падает `ClassNotFoundException: net.fabricmc.devlaunchinjector.Main`.

## Подводные камни машины (Windows)

- Профиль пользователя с кириллицей: `C:\Users\Админ`. В gradle.properties/скриптах пути с кириллицей ломаются — используй `$env:USERPROFILE`, короткий путь `C:/Users/7272~1` или ASCII-пути.
- Внимание при наборе путей: легко напечатать латинское «Адmin» вместо кириллического «Админ» — файл «не найден».
- PowerShell считает **stderr-вывод git/gradle ошибкой**: `exit code 1` при «BUILD SUCCESSFUL» — это норма. Смотри ТЕКСТ вывода, а не код возврата.
- Боевой рабочий терминал — cmd.exe (bash-команды типа `cat` не работают, юзай PowerShell).
- IDEA: run-конфигурации «Minecraft Client/Server» — **Gradle-задачи** (тип GradleRunConfiguration), не Application. В `.idea/gradle.xml` обязательно `delegatedBuild=false`. Если пересоздать их как Application — снова будет ClassNotFound devlaunchinjector. После правок `.idea` перезапусти IDEA.

## Как портировать код из arbuz в Lumen

arbuz написан в **Mojang/intermediary-маппингах** (`class_1268`, `method_6115`, `field0xxx`/`method0xxx` — обфусцированные декомпилятором), Lumen — в **Yarn**. Соответствия, которые уже встречались:

| arbuz (Mojang/intermediary) | Lumen (Yarn) |
|---|---|
| `ServerboundMovePlayerPacket.PosRot` | `PlayerMoveC2SPacket.Full` |
| `ServerboundPlayerActionPacket` | `PlayerActionC2SPacket` |
| `BlockPos.containing(vec3)` | `BlockPos.ofFloored(vec3)` |
| `class_2246.field_10343` | `Blocks.COBWEB` |
| `method_6115` | `isUsingItem()` |
| `method_6128` | `isGliding()` |
| `method_18798 / method_18800` | `getVelocity() / setVelocity(...)` |
| `method_36454 / method_36455` | `getYaw() / getPitch()` |
| `method_24828` | `isOnGround()` |
| `field_1903/field_1832/field_1894/field_1881/field_1913/field_1849` | `options.jumpKey/sneakKey/forwardKey/backKey/rightKey/leftKey` |
| `class_2708` | `PlayerPositionLookS2CPacket` (сетбэк) |

- Точные сигнатуры проверяй через javap по замапленному jar:
  `C:\Users\Админ\.jdks\corretto-21.0.12.1\bin\javap.exe -cp <jar> net.minecraft....`
  jar лежит в `.gradle\loom-cache\minecraftMaven\net\minecraft\minecraft-merged-*\*.jar`.
- Расшифровка intermediary-имён: tiny-маппинг `.gradle\loom-cache\source_mappings\*.tiny` (формат `named→official→intermediary`, интермедиари — ПОСЛЕДНИЙ столбец, ищи `\tmethod_XXXX$`).
- `field0xxx`/`method0xxx` арбуза — обфускация декомпилятора: смысл восстанавливай по контексту использования.
- У арбуза свои системы событий (`PlayerTickEvent`, orbit `@EventHandler`) — переноси ЛОГИКУ, а не каркас: в Lumen события через `@EventLink` (`EventPacket`, `EventUpdate`, `EventTickPre`, `EventSlowWalking`, `EventMoveInput`...).

## Архитектура Lumen (минимум для навигации)

- Точка входа: `dlc.lumen.Lumen`. Модули: `client/modules/impl/{combat,movement,player,misc,render}`. Настройки: `ModeSetting`, `BooleanSetting`, `FloatSetting`, `ListSetting` (пакет `client/modules/settings/implement`).
- События: `@EventLink` + хендлер с параметром события. ВАЖНО: клавиши в игре обрабатываются только когда нет открытого экрана (`KeyboardMixin` → `KeyBoardUtils.call` при `mc.currentScreen == null`).
- **ClickGUI**: `client/ui/modern/ModernGui.java`, открывается на **Right Shift** (key 344, захардкожено в `KeyBoardUtils.call`).
- **Главное меню**: `client/ui/mainmenu/LumenMenuScreen.java` (фон `textures/mainmenu/menu_bg.png`, карточки `single_bg.png`/`multi_bg.png`, кнопка «Сменить аккаунт» открывает AccountGuiScreen).
- **Аккаунт-свитчер**: `client/ui/mainmenu/account/AccountGuiScreen.java` (менеджер — `AccountGuiScreen.MANAGER`, смена сессии через рефлексию `Session`).
- **KillAura** (`Aura.java`, ~1500 строк): настройка `rotationType` («Ротация») выбирает профиль из `components/rotations/*` (наследники `RotationsSystem`, override `updateRotations(LivingEntity)`). Условия атаки: `checkState10()` = attack cooldown + raycast из РЕАЛЬНОГО прицела; для режима ReallyWorld добавлен конус 22° по отправленной на сервер ротации (`checkState11()`).
- **Логи игры**: `run/logs/latest.log` (там же чат — бан-броадкасты видны там). Краш-репорты: `run/crash-reports/`.
- Рендер-хелперы: `RenderUtils` (drawImage, drawRoundedRect, drawBlur), шрифты MSDF `Fonts.getFont("suisse"|"sf_regular"|"icon", размер)`, цвета `ColorUtils` (rgba, setAlphaColor, getThemeColor).

## История: что уже сделано (13.09.2026, одним днём)

1. **Инфраструктура**: установлен git; репозиторий связан с GitHub; Gradle 9.3.0 → 8.12.1 (фикс devlaunchinjector); Gradle-демон переведён на JDK 21; IDEA run-конфиги переведены на Gradle-задачи + `delegatedBuild=false` (в обоих проектах).
2. **Главное меню**: фон заменён на арт с миньоном (lanczos+unsharp, потом гауссово размытие σ=8, 2560×1440); карточки «Одиночная»/«Сетевая» уменьшены (150×100), фон «Сетевой» — Энд с чёрной дырой, «Одиночной» — сакура; кнопка аккаунт-свитчера вместо поля «Введите ник»; шапка «Выбранный аккаунт» с ником по центру; текст «Протяни, чтобы выйти» сделан белым (был красный EXIT_RED).
3. **Аккаунт-свитчер**: весь текст белый; никнеймы только ASCII (`[A-Za-z0-9_]`, фильтр ввода + валидация при добавлении) — кириллица запрещена.
4. **Модули (порты из arbuz + новое)**:
   - Velocity: режимы `Lumen` (проценты), `New Grim` (отмена отдачи + спуф Full-позиции и StopDestroy), `NewGrimV2` (как arbuz: после сетбэка `PlayerPositionLookS2CPacket` пауза 5 пакетов, в воде/лаве выкл);
   - NoSlow: режим `MultiServer` (отмена замедления на тиках 1–2 из 3, на третьем — форс спринта);
   - KillAura: профиль ротации `ReallyWorld` (плавный пропорциональный догон до 40°/тик, синус-джиттер ±6° на дальних углах, флик вниз каждый 50 тиков; атака разрешена по конусу 22° от отправленной ротации);
   - NoWeb: реализован с нуля режим `NoWeb ReallyWorld` (в паутине: Y-скорость в 0, прыжок 0.9, шифт −0.9, WASD-движение на 0.21).
5. **Известный инцидент**: при тестах на сервере SpookyTime аккаунт `sad11234sssa` получил бан [AC+] на 14 дней (причина — обход анти-чита). Тестируй обходы аккуратно, лучше на альтах.

## Стиль работы (чтобы делал «как он»)

- Сначала разбирайся в коде (читай, декодируй маппинги), потом правь. Не выдумывай API — проверяй сигнатуры javap'ом.
- После каждого изменения — компиляция. Сломалось — чини до успешной, не коммить сломанное.
- Визуальная проверка UI: скриншотить окно игры через PowerShell (`ShowWindow` + `SetForegroundWindow`, снимок окна), а не на словах.
- К рестарту игры: если игрок в мире — сначала спросить/не убивать процесс (`taskkill /F` по java с `devlaunchinjector` в командной строке). Рестарт нужен только если правки требуют перезапуска.
- Отвечай пользователю на русском, коротко и по делу, итог — в конце сообщения.
- Правки `.idea` в git не попадают (в `.gitignore`) — это машинно-специфичное.

## Куда что добавлять (частые запросы пользователя)

- Новый режим модуля = новая опция в `ModeSetting` модуля + ветка логики. Примеры в `NoSlow`/`Velocity`.
- Новый профиль ротации = класс в `components/rotations` + опция в `Aura.rotationType` + поле в `Aura` + ветка в цепочке выбора (~строка 541).
- Всё новое — коммит в `cert-dev` и push. Владелец заберёт обновления через `play.bat` (он сам делает git pull).
