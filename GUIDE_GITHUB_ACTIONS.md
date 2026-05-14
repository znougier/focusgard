# 📱 Installer FocusGuard sans PC — Guide GitHub Actions

> Temps estimé : **15 minutes** — tout depuis ton téléphone

---

## 🗺️ Vue d'ensemble

```
Toi (téléphone)          GitHub (cloud)              Toi (téléphone)
      │                       │                             │
      │── Upload ZIP ────────>│                             │
      │                       │── Compile l'APK (~5 min) ──│
      │                       │                             │
      │<── Télécharge APK ────│                             │
      │                                                     │
      └── Installe l'APK sur ton téléphone ─────────────────┘
```

---

## ÉTAPE 1 — Créer un compte GitHub gratuit

1. Ouvre **github.com** dans ton navigateur
2. Appuie sur **"Sign up"** (s'inscrire)
3. Entre :
   - Une adresse email
   - Un mot de passe
   - Un nom d'utilisateur (ex: `tonprenom2024`)
4. Vérifie ton email et confirme ton compte
5. Sur la question "How many team members?" → choisis **"Just me"**
6. Sur "What are you interested in?" → passe cette étape (Skip)

✅ Tu es maintenant sur la page d'accueil de GitHub

---

## ÉTAPE 2 — Créer un dépôt (espace de stockage)

1. Appuie sur le **"+"** en haut à droite
2. Sélectionne **"New repository"**
3. Remplis :
   - **Repository name** : `focusguard`
   - **Description** : `Mon app FocusGuard` (optionnel)
   - Coche **"Public"** (obligatoire pour le plan gratuit)
   - ✅ Coche **"Add a README file"**
4. Appuie sur **"Create repository"** (bouton vert)

✅ Tu vois maintenant la page de ton dépôt avec un fichier README.md

---

## ÉTAPE 3 — Extraire le ZIP sur ton téléphone

Avant d'uploader, il faut extraire le ZIP FocusGuard :

### Sur Android :
1. Va dans tes **Téléchargements** (appli Fichiers ou Mes Fichiers)
2. Appuie longtemps sur **FocusGuard.zip**
3. Sélectionne **"Extraire"** ou **"Décompresser"**
4. Un dossier **FocusGuard** apparaît à côté du ZIP

---

## ÉTAPE 4 — Uploader les fichiers sur GitHub

GitHub permet d'uploader des fichiers depuis le navigateur mobile.

### 4a — Uploader le dossier `.github/workflows/`

Ce dossier contient la "recette" de compilation — c'est le plus important.

1. Sur la page de ton dépôt, appuie sur **"Add file"** → **"Upload files"**
2. Appuie sur **"choose your files"**
3. Navigue jusqu'au dossier extrait `FocusGuard/.github/workflows/`
4. Sélectionne le fichier **`build.yml`**
5. Dans le champ "Commit message" en bas, écris : `Ajout GitHub Actions`
6. Appuie sur **"Commit changes"** (bouton vert)

> ⚠️ **Important** : GitHub ne peut pas créer automatiquement les sous-dossiers via l'interface mobile.
> Tu devras créer le chemin `.github/workflows/build.yml` manuellement :
>
> 1. Appuie sur **"Add file"** → **"Create new file"**
> 2. Dans le champ du nom, tape exactement : `.github/workflows/build.yml`
>    (quand tu tapes le `/`, GitHub crée automatiquement le sous-dossier)
> 3. Copie-colle le contenu du fichier `build.yml` dans la zone de texte
> 4. Appuie sur **"Commit new file"**

### Contenu du fichier build.yml à coller :
```yaml
name: Build FocusGuard APK

on:
  push:
    branches: [ main, master ]
  workflow_dispatch:

jobs:
  build:
    runs-on: ubuntu-latest
    steps:
      - name: Télécharger le code
        uses: actions/checkout@v4
      - name: Installer Java 17
        uses: actions/setup-java@v4
        with:
          java-version: '17'
          distribution: 'temurin'
      - name: Rendre Gradle exécutable
        run: chmod +x gradlew
      - name: Compiler l'APK
        run: ./gradlew assembleDebug
      - name: Sauvegarder l'APK
        uses: actions/upload-artifact@v4
        with:
          name: FocusGuard-APK
          path: app/build/outputs/apk/debug/app-debug.apk
          retention-days: 30
```

---

### 4b — Uploader tous les autres fichiers du projet

1. Depuis la page de ton dépôt, appuie sur **"Add file"** → **"Upload files"**
2. Appuie sur **"choose your files"**
3. Sélectionne **tous** les fichiers/dossiers du dossier `FocusGuard/` extrait
   - `app/` (dossier)
   - `build.gradle`
   - `settings.gradle`
   - `gradlew`
   - `GUIDE_INSTALLATION.md`
4. Attends que tout soit uploadé (peut prendre 1-2 min selon ta connexion)
5. Écris dans "Commit message" : `Ajout du projet FocusGuard`
6. Appuie sur **"Commit changes"**

> 💡 **Astuce** : Si l'upload de dossiers entiers ne fonctionne pas sur mobile, utilise l'application **GitHub Mobile** (disponible sur le Play Store) — elle gère mieux les uploads de dossiers.

---

## ÉTAPE 5 — Lancer la compilation

Dès que tu as commité les fichiers (étape 4), GitHub lance **automatiquement** la compilation !

### Suivre la progression :
1. Sur la page de ton dépôt, appuie sur l'onglet **"Actions"**
2. Tu vois un workflow **"Build FocusGuard APK"** en cours (icône orange ⏳)
3. Appuie dessus pour voir les détails en temps réel
4. Attends ~5-8 minutes que ça devienne vert ✅

### Si c'est rouge ❌ :
- Appuie sur le workflow rouge pour voir l'erreur
- La cause la plus fréquente : un fichier manquant ou mal placé
- Vérifie que `gradlew` est bien à la racine du projet (pas dans un sous-dossier)

---

## ÉTAPE 6 — Télécharger l'APK

Une fois la compilation réussie (✅ vert) :

1. Reste sur la page du workflow réussi
2. Fais défiler vers le bas jusqu'à la section **"Artifacts"**
3. Tu vois **"FocusGuard-APK"**
4. Appuie dessus → ça télécharge un fichier **FocusGuard-APK.zip**
5. Extrais ce ZIP → tu obtiens **`app-debug.apk`**

---

## ÉTAPE 7 — Installer l'APK sur ton téléphone

Android bloque par défaut l'installation d'APK extérieurs au Play Store.
Il faut l'autoriser une fois :

### Autoriser les sources inconnues :
1. Va dans **Paramètres** → **Applications**
2. Appuie sur les **3 points** (menu) → **"Accès spéciaux"**
3. **"Installer des applis inconnues"**
4. Trouve ton navigateur (Chrome, Firefox...) → active **"Autoriser depuis cette source"**

### Installer l'APK :
1. Ouvre tes **Téléchargements**
2. Appuie sur **`app-debug.apk`**
3. Appuie sur **"Installer"**
4. Si Android demande confirmation → **"Installer quand même"**

✅ **FocusGuard est installé !** Tu le trouves dans ta liste d'applications.

---

## 🔄 Pour les mises à jour futures

Si tu veux une nouvelle version avec des modifications :
1. Retourne sur github.com → ton dépôt `focusguard`
2. Modifie ou remplace les fichiers concernés
3. Commit → GitHub recompile automatiquement
4. Retélécharge le nouvel APK depuis l'onglet Actions

---

## ❓ Problèmes fréquents

**"Permission denied" sur gradlew :**
→ Normal, GitHub le corrige automatiquement avec la ligne `chmod +x gradlew`

**Le workflow ne se lance pas :**
→ Va dans Actions → "I understand my workflows, go ahead and enable them"

**L'APK s'installe mais l'app plante au démarrage :**
→ Vérifie que tu as bien accordé la permission "Accès aux données d'utilisation" dans les paramètres Android

**GitHub dit "This file is too large" :**
→ Pas de problème avec FocusGuard, les fichiers sont très légers (< 1 MB)

---

*FocusGuard — Guide GitHub Actions v1.0*
