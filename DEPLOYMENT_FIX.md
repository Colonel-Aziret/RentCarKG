# Исправление проблемы деплоя на DigitalOcean

## Проблема
Приложение падает с ошибкой:
```
Could not resolve placeholder 'token.signing.key' in value "${token.signing.key}"
```

## Причина
На сервере Spring Boot запускается с профилем "default", но отсутствует главный файл `application.properties` с базовой конфигурацией.

## Решение

### 1. Пересоберите проект с исправлениями
```bash
# Локально выполните:
git add .
git commit -m "fix: add application.properties for production deployment"
git push origin main
```

### 2. На сервере обновите код и пересоберите
```bash
# На вашем DigitalOcean сервере:
cd /path/to/your/project
git pull origin main
./mvnw clean package -DskipTests
```

### 3. Настройте environment переменные на сервере
Создайте файл `/etc/systemd/system/rentcarkg.service` или обновите существующий:

```ini
[Unit]
Description=RentCarKG Backend Application
After=network.target

[Service]
Type=forking
User=root
ExecStart=/usr/bin/java -jar /root/rentcarkg-0.0.1-SNAPSHOT.jar
Restart=always
RestartSec=10

# Environment variables for production
Environment=SPRING_PROFILES_ACTIVE=prod
Environment=JWT_SECRET_KEY=VlKr3mmdUr2r2zHjOP7lvseZ+aS9fdPTz3ne0nhjto/0goZEZhIG//QD8d1/tqbsuMvsdKroJFd7DL+Wc151Tg==
Environment=DB_USERNAME=your_db_username
Environment=DB_PASSWORD=your_db_password
Environment=DATABASE_URL=jdbc:postgresql://localhost:5432/rentcarkg
Environment=MAIL_HOST=sandbox.smtp.mailtrap.io
Environment=MAIL_PORT=587
Environment=MAIL_USERNAME=39f8c47070f5f7
Environment=MAIL_PASSWORD=3ba27f7d7417eb
Environment=PORT=8080

[Install]
WantedBy=multi-user.target
```

### 4. Альтернативный способ (если systemd не работает)
Запустите приложение с указанием профиля:
```bash
java -jar rentcarkg-0.0.1-SNAPSHOT.jar --spring.profiles.active=prod
```

### 5. Перезапустите сервис
```bash
sudo systemctl daemon-reload
sudo systemctl restart rentcarkg
sudo systemctl status rentcarkg
```

### 6. Проверьте логи
```bash
journalctl -u rentcarkg -f
```

## Что было исправлено

1. **Добавлен основной `application.properties`** с fallback конфигурацией
2. **Создан `application-prod.properties`** для production
3. **Удален дубликат dependency** в pom.xml
4. **Добавлена поддержка environment переменных** для безопасности

## Безопасность
- JWT_SECRET_KEY должен быть уникальным для production
- Смените пароли базы данных
- Используйте реальный SMTP для email (не Mailtrap)

## Проверка работы
После исправления приложение должно успешно запуститься и быть доступно на порту 8080.