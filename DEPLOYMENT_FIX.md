# ✅ ИСПРАВЛЕНИЕ ПРОБЛЕМЫ ДЕПЛОЯ - ОБНОВЛЕНО

## Проблема
Приложение падает с ошибкой:
```
Could not resolve placeholder 'token.signing.key' in value "${token.signing.key}"
```

## ✅ Решение найдено!

Проблема была в отсутствии корневого файла `application.properties` и неработающих environment переменных в systemd.

## Что нужно сделать на сервере:

### 1. Обновите код на сервере
```bash
cd /root  # или где у вас проект
git pull origin main
```

### 2. Пересоберите проект
```bash
./mvnw clean package -DskipTests
```

### 3. Перезапустите сервис
```bash
systemctl restart rentcarkg
```

### 4. Проверьте запуск
```bash
journalctl -u rentcarkg -f
```

## Что было исправлено:

✅ **Добавлен `application.properties`** - теперь Spring Boot имеет базовую конфигурацию
✅ **Исправлен `application-prod.properties`** - JWT key теперь жестко прописан
✅ **Удален дублирующий dependency** в pom.xml
✅ **Протестировано локально** - JWT ошибка исчезла

## Результат тестирования:

**ДО исправления:**
```
Could not resolve placeholder 'token.signing.key'
```

**ПОСЛЕ исправления:**
```
The following 1 profile is active: "prod"
(приложение запускается, JWT ошибки нет)
```

## Если все еще есть проблемы:

Альтернативный запуск вручную:
```bash
# Остановите systemd сервис
systemctl stop rentcarkg

# Запустите вручную для диагностики
java -jar /root/rentcarkg-0.0.1-SNAPSHOT.jar --spring.profiles.active=prod
```

## Безопасность для production:
- Смените JWT ключ на уникальный для production
- Настройте правильные креды базы данных
- Используйте реальный SMTP сервер

Теперь приложение должно запуститься без ошибок!