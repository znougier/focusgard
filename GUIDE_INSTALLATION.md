# 🛡️ FocusGuard — Guide d'installation complet

## Ce que fait cette application

FocusGuard est une application Android native qui te permet de :
- **Bloquer des applications** par plages horaires (ex : réseaux sociaux bloqués de 6h à 8h le matin)
- **Limiter le temps** d'utilisation par application par jour
- **Voir tes statistiques** d'utilisation des dernières 24h
- **Mode urgence** : débloquer temporairement une app pendant 5 minutes avec ton PIN
- **Dark mode** avec un design soigné

---

## 📋 Ce qu'il te faut

1. Un PC ou Mac (pour compiler)
2. **Android Studio** (gratuit) → https://developer.android.com/studio
3. Ton téléphone Android avec le câble USB

---

## 🚀 Étapes d'installation

### Étape 1 — Installer Android Studio

1. Va sur https://developer.android.com/studio
2. Télécharge la version pour Windows/Mac
3. Installe-le (garde tous les paramètres par défaut)
4. Au premier lancement, il téléchargera automatiquement les outils Android (attends ~10 min)

---

### Étape 2 — Ouvrir le projet FocusGuard

1. Lance Android Studio
2. Clique sur **"Open"** (ou "Open an Existing Project")
3. Navigue jusqu'au dossier `FocusGuard` que tu as téléchargé
4. Clique **OK**
5. Android Studio va synchroniser le projet (attends ~2-3 min la première fois)

---

### Étape 3 — Préparer ton téléphone Android

**Activer le mode développeur :**
1. Va dans **Paramètres** → **À propos du téléphone**
2. Appuie **7 fois** sur **"Numéro de build"**
3. Tu verras "Vous êtes maintenant développeur !"

**Activer le débogage USB :**
1. Va dans **Paramètres** → **Options développeur**
2. Active **"Débogage USB"**
3. Branche ton téléphone au PC avec le câble USB
4. Sur ton téléphone, accepte la popup "Autoriser le débogage USB"

---

### Étape 4 — Lancer l'application sur ton téléphone

1. Dans Android Studio, en haut, tu vois un menu déroulant avec le nom de ton téléphone
2. Clique sur le bouton **▶️ Run** (triangle vert) ou appuie sur **Shift+F10**
3. L'application va se compiler et s'installer automatiquement sur ton téléphone (~2-3 min)

---

### Étape 5 — Configurer FocusGuard

Au premier lancement, l'app te demandera :

**1. Créer ton PIN**
- Entre un code à 4 chiffres minimum
- Confirme-le
- ⚠️ **Note-le quelque part** — c'est le seul moyen de modifier les réglages

**2. Accorder la permission d'usage**
- Une popup s'affichera pour te rediriger vers les paramètres Android
- Dans la liste, trouve **"FocusGuard"** et active l'interrupteur
- Reviens dans l'app

**3. C'est prêt !** Le service de surveillance démarre automatiquement.

---

## 📱 Comment utiliser FocusGuard

### Onglet "Apps" — Ajouter des applications à bloquer
1. Appuie sur le **bouton +** en bas à droite
2. Coche les applications à bloquer (Instagram, TikTok, YouTube, Chrome...)
3. Appuie **Ajouter**
4. Pour chaque app, tu peux :
   - Activer/désactiver le blocage avec l'interrupteur
   - Définir une **limite quotidienne** (ex: 30 min/jour) avec le bouton ⏱️
   - Supprimer l'app de la liste avec le bouton ✕

### Onglet "Plannings" — Bloquer par horaires
1. **Raccourcis rapides :**
   - 🌅 **Matin** → Bloque de 6h à 8h, du lundi au vendredi (parfait pour le réveil)
   - 🌙 **Nuit** → Bloque de 22h à 7h, tous les jours
2. **Créer un planning personnalisé** avec le bouton + :
   - Choisis un nom (ex: "Concentration travail")
   - Sélectionne l'heure de début et de fin
   - Coche les jours de la semaine
   - Choisis si ça s'applique à toutes les apps bloquées ou non

### Onglet "Stats" — Voir ton utilisation
- Voit le temps passé sur chaque app aujourd'hui
- Barre de progression comparative
- Temps d'écran total

### Onglet "Accueil"
- Active/désactive toute la protection d'un coup
- Résumé rapide : apps bloquées, plannings actifs, temps d'écran

---

## 🚨 Mode urgence

Quand une app est bloquée et que tu en as vraiment besoin :
1. Appuie sur **"Mode urgence (5 min)"**
2. Entre ton PIN
3. L'app est débloquée pendant **5 minutes exactement**
4. Après 5 minutes, le blocage reprend automatiquement

---

## ❓ Problèmes fréquents

**L'app ne bloque pas :**
- Vérifie que la permission d'usage est accordée dans Paramètres → Applications → Accès spéciaux → Accès aux données d'utilisation
- Vérifie que la protection est activée (interrupteur sur l'accueil)
- Sur certains téléphones (Xiaomi, Huawei, Samsung), il faut aussi désactiver l'optimisation de batterie pour FocusGuard

**Le service s'arrête :**
- Va dans Paramètres → Applications → FocusGuard → Batterie → "Pas de restriction"
- Sur Xiaomi : Paramètres → Applications → FocusGuard → Autorisations → Démarrage automatique → Activer

**Android Studio ne trouve pas mon téléphone :**
- Réinstalle les drivers USB Android (cherche "ADB drivers" + la marque de ton téléphone)
- Essaie un autre câble USB (certains câbles ne transfèrent que l'alimentation)

---

## 🔧 Générer un APK (pour partager sans Android Studio)

1. Dans Android Studio : **Build** → **Build Bundle(s)/APK(s)** → **Build APK(s)**
2. L'APK se trouve dans `app/build/outputs/apk/debug/app-debug.apk`
3. Transfère ce fichier sur ton téléphone et installe-le
   - ⚠️ Tu devras autoriser "Installation d'applications inconnues" dans les paramètres

---

## 📌 Notes importantes

- FocusGuard fonctionne **en arrière-plan en permanence** via un service de premier plan (la petite notification en haut indique qu'il est actif — c'est normal et requis par Android)
- La surveillance démarre **automatiquement au redémarrage** du téléphone
- Les statistiques sont basées sur les données natives d'Android (très précises)
- Sur Android 12+, le service peut être arrêté par le système si la batterie est très faible

---

*FocusGuard v1.0 — Développé avec ❤️ pour l'autodiscipline numérique*
