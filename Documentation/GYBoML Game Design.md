# Игровой Дизайн GYBoML

### Начало сессии
Считаем, что мы минули процедуры в лобби и сессия успешно создана.\
После этого на экране появляется поле для сражения двух игроков.\
![Экран после создания сессии](https://github.com/zhenyatos/GYBoML/blob/docworks-client/Documentation/gamedesign_image1.png)

Далее так как игра пошаговая необходимо решить, чей ход будет первый.\
Предлагается не пускать всё на волю рандома, а вставить мини-игру, победитель которой будет делать первый ход.\
Как вариант: выводится простенькое выражение, например "47+69",\
вместе с ним выскакивает панель для ввода цифр и кнопка "Подтвердить".\
Суть очень проста: кто раньше введет верный ответ, тот и победитель.\
Если ошиблись оба, то выводится новое выражение.\
Вопрос выбора мини-игры и других деталей остается открытым.\
![Примерный вид мини-игры](https://github.com/zhenyatos/GYBoML/blob/docworks-client/Documentation/gamedesign_image2.png)

### Схема поля боя
Концепт-схема игрового поля боя: \
![Концепт-схема игрового поля боя](https://github.com/zhenyatos/GYBoML/blob/docworks-client/Documentation/gamedesign_image3.png)
1. Высокие башни с пушками находятся по краям экрана.
2. Замки каждого из игроков - непосредственно те объекты, которые будут иметь HP-bar'ы и которые нужно будет оборонять.
3. Нейтральная зона будет представлять из себя прежде всего разграничительную область, где ни один игрок не может строиться. Также ее можно использовать как зону для некоторых событий (см. **Система случайных событий**).
4. Застраиваемые блоки.

### Основные моменты механики
- *Подготовительный этап.* Перед тем, как будет определено, кто делает первый ход, обоим игрокам предлагается n-нное число ресурсов (допустим, золото) на постройку обороны. Оборона представляет собой множество различных блоков. Предлагается сделать этот этап "скрытным", т.е. чтобы противники не видели, каким образом застраивается оппонент (+это поможет проще реализовать подготовительный этап). Когда оба игрока будут готовы, можно начинать розыгрыш первого хода.\
![Концепт-схема подготовительного этапа](https://github.com/zhenyatos/GYBoML/blob/docworks-client/Documentation/gamedesign_image4.png)
- *Управление пушкой.* Предлагается ввести довольно заезженную, но работающую механику, а именно: выбор угла наклона и/или выбор силы выстрела (начальной скорости/импульса? - на усмотрение разработчика физики игры) производить в динамике, т.е., например, при выборе угла наклона пушки относительно линии горизонта она(пушка) будет сама ездить туда-сюда (от 0 до 45 градусов, положим), игрок должен нажать соответствующую кнопку выбора текущего угла в нужный ему момент (аналогично можно ввести катающийся ползунок для силы выстрела). Вводится эта механика прежде всего для исключения в дальнейшем абузов механики со стороны игроков, иначе они могут запомнить некоторые удачные для них параметры выстрела, чтобы всегда попадать точно туда, куда нужно. С такой механикой мы введет какую-никакую погрешность и разнообразие процесса.\
![Динамически изменяющийся угол наклона пушки](https://github.com/zhenyatos/GYBoML/blob/docworks-client/Documentation/gamedesign_image5.png)
- *Фазы.* Игровой процесс предлагается сделать не только пошаговым, но и разбить на фазы. Т.е. на стрельбу игрокам будет выделено, например, 2-3 хода, а затем следует 1 ход дополнительной постройки.
- Установку блоков сделаем не произвольной, т.е. подвешенной в воздухе (а-ля Minecraft), а введем дополнительный тип блоков - опоры. Они будут иметь минимальную прочность (разрушаются снарядом любого вида с одного попадания), но добавляют дополнительную устойчивость для блоков (НЕ прочность). Блоки могут быть установлены либо на землю/друг на друга либо поверх опоры, которая будет их держать. Первая стадия разработки предусматривает наличие только вертикальных опор. Следующая иллюстрация кратко описывает их механику:
1. После установки можно отрегулировать ее высоту.
2. Блок может быть прикреплен к опоре с любой стороны.
3. Наглядная иллюстрация возможной застройки.
4. Запрещаем устанавливать множество опор слишком близко друг к другу (опоры устанавливаются либо на земле либо на блоках-НЕ опорах).\
![Иллюстрации к механике опор](https://github.com/zhenyatos/GYBoML/blob/docworks-client/Documentation/gamedesign_image8.png)
- Башню с пушкой предлагается сделать высокой из следующих соображений. Во-первых, чтобы игрок не стрелял по своим же сооружениями (либо можно исключить возможность френдлифаера). Сюда же стоит добавить, что область постройки стоит ограничить по вертикали. Во-вторых, если мы сделаем башню с пушкой неразрушаемой, то снаряды врага могут отскакивать от нее и лететь назад в сооружения. Таким образом мы (немного искусственно) заставим игрока строить оборону сзади (со стороны своей пушки).\
![Отскок снаряда от башни](https://github.com/zhenyatos/GYBoML/blob/docworks-client/Documentation/gamedesign_image6.png)
- *Завершение игры.* Как было сказано ранее, замок имеет очки здоровья, победителем становится тот, кто первым уничтожит замок оппонента.

### Система случайных событий
Предлагается ввести систему рандомных событий, например:
- в начале какого-то хода может пойти дождь, который имеет пару последствий: горящие снаряды запускать в этот ход будет нельзя, а также все горящие деревенняе блоки мгновенно потухнут (о видах снарядов и блоков позже).
- в нейтральную зону может случайно упасть сундук с сокровищами. Заполучить его можно следующим образом: существует специальный вид снарядов - трос с крюком. Он никак не способен повредить вражеские сооружния, единственное его применение - попасть в сундук и забрать себе. Т.е. таким образом игрок тратит ход на заполучение сундука, и не факт, что попадет (см. *Управление пушкой*). В сундуке в свою очередь должна быть адекватная награда, например, куча золота для построек или какие-нибудь супер-снаряды. С физикой и отрисовкой веревки можно не сильно париться, пусть это будет просто линия, описывающая траекторию снаряда-крюка, а как только крюк приземлится - она исчезнет.

### Дополнительные моменты механики
- Замок может иметь башенку-выступ (см. Концепт-схему игрового поля боя), попадание по которой будет иметь двойной урон.
-  Можно добавить возможность попадания в дуло пушки врага. Тогда его пушка заткнется на 2 хода (т.к. пытаться заткнуть ее на 1 ход попросту невыгодно). Пусть это дуло имеет маленький хитбокс (опять же пытаемся избежать абузов). При забитом дуле строиться игрок все равно сможет, если настанет *фаза постройки.*
-  Предлагается обдумать идею о начислении золота за разрушенные блоки. С одной стороны, если золото будет начисляться разрушевшему, тогда у игрока, потерявшего блок, убудет вдвойне. С другой стороны, если начислять золото игроку, потерявшему блок, то игра рискует затянуться надолго. Первый вариант все же предпочтительнее, но есть и другие варианты:
-  Как один из способов получения золота - рандомно спавнящиеся в нейтральной зоне (подвешенные в воздухе) мешочки с золотом. По ним достаточно просто попасть любым снарядом. Снаряд при попадании летит дальше. Мешочки можно спавнить (и удалять оставшиеся старые) в начале каждой *фазы стрельбы*.\
![Схема попадания снаряда в мешочек золота](https://github.com/zhenyatos/GYBoML/blob/docworks-client/Documentation/gamedesign_image7.png)
- Можно добавить возможность покупки не только блоков, но и снарядов + их продажу (данный процесс возможен во время *фазы постройки*). Выручка с продажи блока может соответствовать его состоянию (избегаем абузов), либо пусть подлежат продаже только неповрежденные блоки.