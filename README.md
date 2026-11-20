# Pagina următoare – Android

Aplicație Android Studio simplă care deschide cursul furnizat, permite autentificarea în WebView și, după apăsarea START, caută butonul cu textul exact „Pagina următoare” și îl apasă o dată la fiecare 9 minute.

## Limitări importante
- Automatizarea rulează numai cât timp aplicația rămâne deschisă și activă. Android poate opri activitatea dacă aplicația este trecută în fundal sau sistemul închide procesul.
- Aplicația menține ecranul aprins cât timp automatizarea este pornită.
- Nu apasă butonul imediat: prima apăsare are loc după 9 minute.
- Dacă butonul nu este găsit, afișează un mesaj și reîncearcă la următorul interval.
- Site-ul poate schimba structura paginii sau poate folosi un buton cu alt text; în acest caz selectorul JavaScript poate necesita ajustări.
- Folosește aplicația doar în măsura permisă de regulile platformei și ale cursului. Nu ocolește autentificarea, testele sau alte verificări ale cursului.

## Cum generezi APK-ul
1. Descarcă și dezarhivează proiectul.
2. Deschide folderul `PaginaUrmatoareAndroid` în Android Studio.
3. Așteaptă sincronizarea Gradle.
4. Alege **Build > Build Bundle(s) / APK(s) > Build APK(s)**.
5. APK-ul de debug va fi în `app/build/outputs/apk/debug/app-debug.apk`.

Necesită Android Studio cu Android Gradle Plugin 8.7.3 și SDK Android 35.

## Compilare online de pe telefon (GitHub Actions)
1. Creează un cont gratuit pe https://github.com dacă nu ai deja.
2. În GitHub, creează un repository nou (de exemplu `PaginaUrmatoareAndroid`).
3. În repository, folosește **Add file > Upload files** și încarcă toate fișierele și folderele din acest proiect, inclusiv folderul ascuns `.github`.
4. Apasă **Commit changes**. Workflow-ul pornește automat după încărcarea pe ramura `main`.
5. Deschide fila **Actions**, apoi rularea „Build Android APK”.
6. Când rularea devine verde, deschide-o și descarcă artifact-ul **PaginaUrmatoare-debug-apk**.
7. Dezarhivează artifact-ul descărcat și instalează `app-debug.apk` pe telefon. Dacă Android cere permisiune, permite instalarea din browser/Files pentru această instalare.

Notă: workflow-ul folosește GitHub Actions. Nu este nevoie să instalezi Android Studio pe telefon.
