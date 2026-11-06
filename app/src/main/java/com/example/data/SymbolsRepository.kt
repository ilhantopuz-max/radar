package com.example.data

import com.example.model.StockItem

object SymbolsRepository {

    val allSymbols: List<StockItem> by lazy {
        val list = mutableListOf<StockItem>()

        // Helper to add BIST stocks
        fun addBist(group: String, symbols: List<Pair<String, Double>>, sector: String = "BIST Sanayi & Ticaret") {
            symbols.forEach { (ticker, basePrice) ->
                val name = getCompanyName(ticker)
                list.add(
                    StockItem(
                        ticker = ticker,
                        fullSymbol = "BIST:$ticker",
                        name = name,
                        group = group,
                        sector = getSector(ticker, sector),
                        basePrice = basePrice,
                        currency = "₺"
                    )
                )
            }
        }

        // GRUP 1
        addBist("Grup 1", listOf(
            "A1CAP" to 32.40, "ACSEL" to 142.50, "ADEL" to 395.0, "ADESE" to 2.18, "ADGYO" to 28.50,
            "AEFES" to 198.0, "AFYON" to 14.20, "AGESA" to 95.50, "AGHOL" to 310.0, "AGROT" to 17.80,
            "AGYO" to 9.45, "AHGAZ" to 13.60, "AKBNK" to 58.20, "AKCNS" to 145.0, "AKENR" to 18.20,
            "AKFGY" to 4.85, "AKFYE" to 24.10, "AKGRT" to 7.90, "AKMGY" to 290.0, "AKSA" to 98.40,
            "AKSEN" to 36.50, "AKSGY" to 16.80, "AKSUE" to 18.70, "AKYHO" to 7.30, "ALARK" to 102.50,
            "ALBRK" to 4.70, "ALCAR" to 1120.0, "ALCTL" to 105.0, "ALFAS" to 76.50, "ALGYO" to 44.20,
            "ALKA" to 28.10, "ALKIM" to 35.80, "RUZYE" to 21.40, "ALTIN" to 24.80, "ALVES" to 43.60,
            "ANELE" to 19.30, "ANGEN" to 13.90, "ANHYT" to 88.0, "ANSGR" to 82.50, "ARASE" to 55.40
        ))

        // GRUP 2
        addBist("Grup 2", listOf(
            "ARCLK" to 158.0, "ARDYZ" to 42.60, "ARENA" to 38.20, "ARSAN" to 15.40, "ARTMS" to 26.80,
            "ARZUM" to 46.50, "ASELS" to 62.10, "ASGYO" to 15.90, "ASTOR" to 94.20, "ASUZU" to 235.0,
            "ATAGY" to 12.30, "ATAKP" to 48.0, "ATATP" to 112.0, "ATEKS" to 138.0, "ATLAS" to 7.80,
            "ATSYH" to 84.0, "AVGYO" to 9.20, "AVHOL" to 34.0, "AVOD" to 4.10, "AVPGY" to 44.50,
            "AVTUR" to 14.60, "AYCES" to 495.0, "AYDEM" to 31.80, "AYEN" to 34.20, "AYES" to 39.50,
            "AYGAZ" to 175.0, "AZTEK" to 128.0, "BAGFS" to 26.50, "BAKAB" to 48.20, "BALAT" to 24.50,
            "BANVT" to 275.0, "BARMA" to 19.80, "BASCM" to 14.50, "BASGZ" to 20.30, "BAYRK" to 32.10,
            "BEGYO" to 4.25, "BERA" to 17.90, "BEYAZ" to 28.40, "BFREN" to 985.0, "BIENY" to 41.20
        ))

        // GRUP 3
        addBist("Grup 3", listOf(
            "BIGCH" to 34.20, "BIMAS" to 485.0, "BINHO" to 410.0, "BIOEN" to 18.90, "BIZIM" to 39.80,
            "BJKAS" to 6.85, "BLCYT" to 17.40, "BMSCH" to 19.30, "BMSTL" to 36.50, "BNTAS" to 9.60,
            "BOBET" to 32.40, "BORLS" to 38.0, "BORSK" to 33.60, "BOSSA" to 11.20, "BRISA" to 114.0,
            "BRKO" to 5.40, "BRKSN" to 4.99, "BRKVY" to 125.0, "BRLSM" to 21.60, "BRMEN" to 6.30,
            "BRSAN" to 480.0, "BRYAT" to 2450.0, "BSOKE" to 28.50, "BTCIM" to 165.0, "BUCIM" to 8.40,
            "BURCE" to 220.0, "BURVA" to 175.0, "BVSAN" to 88.50, "BYDNR" to 27.60, "CANTE" to 16.80,
            "CASA" to 115.0, "CATES" to 52.0, "CCOLA" to 595.0, "CELHA" to 36.40, "CEMAS" to 4.80
        ))

        // GRUP 4
        addBist("Grup 4", listOf(
            "CEMTS" to 11.50, "CEOEM" to 23.40, "CIMSA" to 35.80, "CLEBI" to 1650.0, "CMBTN" to 1340.0,
            "CMENT" to 480.0, "CONSE" to 8.90, "COSMO" to 230.0, "CRDFA" to 9.80, "CRFSA" to 138.0,
            "CUSAN" to 21.50, "CVKMD" to 445.0, "CWENE" to 240.0, "TRHOL" to 18.20, "DAGI" to 12.80,
            "DAPGM" to 42.0, "DARDL" to 5.80, "DENGE" to 3.45, "DERHL" to 19.20, "DERIM" to 24.50,
            "DESA" to 23.10, "DESPC" to 41.50, "DEVA" to 92.0, "DGATE" to 38.60, "DGGYO" to 38.0,
            "DGNMO" to 13.40, "DIRIT" to 8.20, "DITAS" to 26.40, "DMRGD" to 15.60, "DMSAS" to 10.80,
            "DNISI" to 14.50, "DOAS" to 275.0, "BIGTK" to 19.80, "DOCO" to 4650.0, "DOFER" to 39.50,
            "DOGUB" to 18.20, "DOHOL" to 14.60, "DOKTA" to 48.0, "DURDO" to 16.40, "DYOBY" to 34.50
        ))

        // GRUP 5
        addBist("Grup 5", listOf(
            "DZGYO" to 6.40, "EBEBK" to 49.50, "ECILC" to 51.50, "ECZYT" to 285.0, "EDATA" to 18.90,
            "EDIP" to 16.50, "EGEEN" to 11800.0, "EGEPO" to 25.40, "EGGUB" to 71.0, "EGPRO" to 195.0,
            "EGSER" to 6.30, "EKGYO" to 11.80, "EKIZ" to 64.0, "EKOS" to 45.20, "EKSUN" to 68.0,
            "ELITE" to 58.50, "EMKEL" to 48.0, "EMNIS" to 195.0, "ENERY" to 148.0, "ENJSA" to 61.50,
            "ENKAI" to 44.80, "ENSRI" to 27.50, "EPLAS" to 12.40, "ERBOS" to 195.0, "ERCB" to 84.0,
            "EREGL" to 51.20, "ERSU" to 32.0, "ESCAR" to 295.0, "ESCOM" to 52.0, "ESEN" to 23.80,
            "ETILR" to 22.10, "ETYAT" to 14.80, "EUHOL" to 4.20, "EUKYO" to 12.30, "EUPWR" to 98.0,
            "EUREN" to 12.90, "EUYO" to 11.40, "EYGYO" to 26.50, "FADE" to 18.40, "FENER" to 108.0
        ))

        // GRUP 6
        addBist("Grup 6", listOf(
            "FLAP" to 13.80, "FMIZP" to 290.0, "FONET" to 18.40, "FORMT" to 4.75, "FORTE" to 68.0,
            "FRIGO" to 8.60, "FROTO" to 1040.0, "FZLGY" to 18.20, "GARAN" to 118.0, "GARFA" to 84.0,
            "GEDIK" to 14.50, "GEDZA" to 38.0, "GENIL" to 78.50, "GENTS" to 8.70, "GEREL" to 34.0,
            "GESAN" to 49.50, "GIPTA" to 33.40, "GLBMD" to 48.0, "GLCVY" to 39.50, "GLRYH" to 11.20,
            "GLYHO" to 13.90, "GMTAS" to 12.40, "GOKNR" to 28.50, "GOLTS" to 390.0, "GOODY" to 24.80,
            "GOZDE" to 27.40, "GRNYO" to 11.50, "GRSEL" to 145.0, "GRTHO" to 22.0, "GSDDE" to 11.80,
            "GSDHO" to 4.35, "GSRAY" to 8.90, "GUBRF" to 175.0, "GWIND" to 27.80, "GZNMI" to 38.0,
            "HALKB" to 16.50, "HATEK" to 17.20, "HATSN" to 58.0, "HDFGS" to 2.85, "HEDEF" to 2.95
        ))

        // GRUP 7
        addBist("Grup 7", listOf(
            "HEKTS" to 3.85, "HKTM" to 68.0, "HLGYO" to 4.75, "HTTBT" to 74.0, "HUBVC" to 8.80,
            "HUNER" to 4.90, "HURGZ" to 6.20, "ICBCT" to 12.80, "ICUGS" to 24.0, "IDGYO" to 6.90,
            "IEYHO" to 4.60, "IHAAS" to 18.20, "IHEVA" to 3.10, "IHGZT" to 1.85, "IHLAS" to 1.45,
            "IHLGM" to 1.70, "IHYAY" to 2.80, "IMASM" to 15.60, "INDES" to 8.70, "INFO" to 13.20,
            "INGRM" to 495.0, "INTEM" to 210.0, "INVEO" to 39.0, "INVES" to 340.0, "TRENJ" to 24.0,
            "ISATR" to 145000.0, "ISBIR" to 125.0, "ISBTR" to 115000.0, "ISCTR" to 13.80, "ISDMR" to 37.50,
            "ISFIN" to 12.40, "ISGSY" to 24.0, "ISGYO" to 16.50, "ISKPL" to 12.30, "ISKUR" to 2250000.0,
            "ISMEN" to 38.50, "ISSEN" to 15.20, "IZENR" to 25.80, "IZFAS" to 8.40, "IZINV" to 42.0
        ))

        // GRUP 8
        addBist("Grup 8", listOf(
            "IZMDC" to 6.45, "JANTS" to 230.0, "KAPLM" to 165.0, "KAREL" to 12.90, "KARSN" to 11.40,
            "KARTN" to 118.0, "A1YEN" to 28.50, "KATMR" to 2.45, "KAYSE" to 29.80, "KBORU" to 84.50,
            "KCAER" to 14.80, "KCHOL" to 215.0, "KENT" to 950.0, "KERVN" to 2.40, "BESLR" to 45.0,
            "KFEIN" to 94.0, "KGYO" to 38.0, "KIMMR" to 11.20, "KLGYO" to 4.95, "KLKIM" to 26.50,
            "KLMSN" to 28.0, "KLNMA" to 38.50, "KLRHO" to 165.0, "KLSER" to 46.0, "KLSYN" to 6.20,
            "KMPUR" to 44.50, "KNFRT" to 14.80, "KONKA" to 46.0, "KONTR" to 54.0, "KONYA" to 8850.0,
            "KOPOL" to 46.50, "KORDS" to 88.0, "TRMET" to 36.0, "TRALT" to 48.0, "KRDMA" to 26.50,
            "KRDMB" to 24.0, "KRDMD" to 27.80, "KRGYO" to 34.0, "KRONT" to 24.50, "KRPLS" to 8.60
        ))

        // GRUP 9
        addBist("Grup 9", listOf(
            "KRSTL" to 8.90, "KRTEK" to 32.0, "KRVGD" to 2.85, "KSTUR" to 6250.0, "KTLEV" to 58.0,
            "KTSKR" to 84.0, "KUTPO" to 78.0, "KUVVA" to 48.0, "KUYAS" to 54.0, "KZBGY" to 21.50,
            "KZGYO" to 18.90, "LIDER" to 78.0, "LIDFA" to 8.40, "LINK" to 485.0, "LKMNH" to 19.80,
            "LMKDC" to 28.0, "LOGO" to 96.50, "LRSHO" to 16.50, "LUKSK" to 98.0, "MAALT" to 1250.0,
            "MACKO" to 78.0, "MAGEN" to 14.80, "MAKIM" to 49.0, "MAKTK" to 8.40, "MANAS" to 12.30,
            "MARBL" to 16.50, "MARKA" to 85.0, "MARTI" to 5.80, "MAVI" to 98.50, "MEDTR" to 38.0,
            "MEGAP" to 6.40, "MEGMT" to 34.0, "MEKAG" to 68.0, "MEPET" to 12.80, "MERCN" to 13.50,
            "MERIT" to 175.0, "MERKO" to 14.20, "METRO" to 2.85, "METUR" to 14.80, "MGROS" to 510.0
        ))

        // GRUP 10
        addBist("Grup 10", listOf(
            "MHRGY" to 4.85, "MIATK" to 56.0, "LYDHO" to 18.50, "MMCAS" to 18.90, "MNDRS" to 11.20,
            "MNDTR" to 16.50, "MOBTL" to 4.25, "MOGAN" to 11.80, "MPARK" to 345.0, "MRGYO" to 8.40,
            "MRSHL" to 2150.0, "MSGYO" to 12.40, "MTRKS" to 68.0, "MTRYO" to 8.90, "MZHLD" to 18.50,
            "NATEN" to 54.0, "NETAS" to 78.0, "NIBAS" to 28.0, "NTGAZ" to 17.50, "NTHOL" to 42.0,
            "NUGYO" to 14.20, "NUHCM" to 295.0, "OBAMS" to 34.0, "OBASE" to 48.0, "ODAS" to 7.80,
            "OFSYM" to 98.0, "ONCSM" to 185.0, "ORCAY" to 12.40, "ORGE" to 84.0, "ORMA" to 175.0,
            "OSMEN" to 385.0, "OSTIM" to 11.80, "OTKAR" to 540.0, "OTTO" to 685.0, "OYAKC" to 61.0,
            "OYAYO" to 42.0, "OYLUM" to 9.20, "OYYAT" to 78.0, "OZGYO" to 6.80, "OZKGY" to 11.50
        ))

        // GRUP 11
        addBist("Grup 11", listOf(
            "OZRDN" to 48.0, "OZSUB" to 34.0, "PAGYO" to 44.0, "PAMEL" to 135.0, "PAPIL" to 98.0,
            "PARSN" to 95.0, "PASEU" to 64.0, "PATEK" to 142.0, "PCILT" to 18.50, "PEKGY" to 8.40,
            "PENGD" to 8.90, "PENTA" to 18.20, "PETKM" to 21.40, "PETUN" to 84.0, "PGSUS" to 228.0,
            "PINSU" to 21.0, "PKART" to 98.0, "PKENT" to 340.0, "PLTUR" to 16.50, "PNLSN" to 78.0,
            "PNSUT" to 94.0, "POLHO" to 13.80, "POLTK" to 14500.0, "PRDGS" to 8.20, "PRKAB" to 38.0,
            "PRKME" to 24.50, "PRZMA" to 48.0, "PSDTC" to 68.0, "PSGYO" to 6.80, "QNBTR" to 285.0,
            "QNBFK" to 84.0, "QUAGR" to 3.85, "RALYH" to 195.0, "RAYSG" to 380.0, "REEDR" to 24.50,
            "RNPOL" to 28.0, "RODRG" to 48.0, "RTALB" to 16.50, "RUBNS" to 34.0
        ))

        // GRUP 12
        addBist("Grup 12", listOf(
            "RYGYO" to 38.0, "RYSAS" to 58.0, "SAFKR" to 48.0, "SAHOL" to 96.50, "SAMAT" to 42.0,
            "SANEL" to 28.0, "SANFM" to 48.0, "SANKO" to 38.0, "SARKY" to 34.50, "SASA" to 4.45,
            "SAYAS" to 78.0, "SDTTR" to 275.0, "SEGYO" to 4.80, "SEKFK" to 18.50, "SEKUR" to 24.0,
            "SELEC" to 56.0, "SELGD" to 38.0, "SELVA" to 14.80, "SEYKM" to 11.20, "SILVR" to 19.50,
            "SISE" to 46.80, "SKBNK" to 4.90, "SKTAS" to 5.40, "SKYLP" to 68.0, "SKYMD" to 24.0,
            "SMART" to 14.50, "SMRTG" to 48.0, "SNGYO" to 4.35, "SNICA" to 38.0, "SNKRN" to 48.0,
            "SNPAM" to 125.0, "SODSN" to 175.0, "SOKE" to 14.20, "SOKM" to 52.0, "SONME" to 88.0,
            "SRVGY" to 340.0, "SUMAS" to 480.0, "SUNTK" to 18.50, "SURGY" to 48.0, "SUWEN" to 24.50
        ))

        // GRUP 13
        addBist("Grup 13", listOf(
            "TABGD" to 148.0, "TARKM" to 640.0, "TATEN" to 34.0, "TATGD" to 28.50, "TAVHL" to 245.0,
            "TBORG" to 115.0, "TCELL" to 92.50, "TDGYO" to 14.50, "TEKTU" to 4.80, "TERA" to 48.0,
            "LYDYE" to 28.0, "TEZOL" to 19.50, "TGSAS" to 42.0, "THYAO" to 295.0, "TKFEN" to 48.0,
            "TKNSA" to 36.50, "TLMAN" to 148.0, "TMPOL" to 118.0, "TMSN" to 128.0, "TNZTP" to 58.0,
            "TOASO" to 235.0, "TRCAS" to 28.0, "TRGYO" to 44.0, "TRILC" to 26.50, "TSGYO" to 14.80,
            "TSKB" to 11.90, "TTKOM" to 54.0, "TTRAK" to 780.0, "TUCLK" to 14.50, "TUKAS" to 7.80,
            "TUPRS" to 164.0, "TUREX" to 48.0, "TURGG" to 680.0, "TURSG" to 84.0, "UFUK" to 185.0,
            "ULAS" to 34.0, "ULKER" to 135.0, "ULUFA" to 14.20, "ULUSE" to 215.0
        ))

        // GRUP 14
        addBist("Grup 14", listOf(
            "ULUUN" to 32.0, "UMPAS" to 14.80, "UNLU" to 18.50, "USAK" to 11.20, "INTEK" to 24.0,
            "VAKBN" to 21.80, "VAKFN" to 4.80, "VAKKO" to 98.0, "VANGD" to 16.50, "VBTYZ" to 48.0,
            "VERTU" to 48.0, "VERUS" to 320.0, "VESBE" to 21.50, "VESTL" to 74.0, "VKFYO" to 24.0,
            "VKGYO" to 3.85, "VKING" to 48.0, "VRGYO" to 48.0, "YAPRK" to 84.0, "YATAS" to 34.0,
            "YAYLA" to 16.50, "YBTAS" to 145000.0, "YEOTK" to 198.0, "YESIL" to 6.20, "YGGYO" to 48.0,
            "YKBNK" to 29.50, "YKSLN" to 14.80, "YONGA" to 68.0, "YUNSA" to 74.0, "YYAPI" to 3.45,
            "YYLGD" to 13.80, "ZEDUR" to 84.0, "ZOREN" to 5.40, "ZRGYO" to 6.80, "ODINE" to 78.0,
            "ENTRA" to 14.50, "RGYAS" to 165.0, "LILAK" to 28.50, "KOTON" to 26.0
        ))

        // GRUP 15
        addBist("Grup 15", listOf(
            "ALTNY" to 98.0, "KOCMT" to 34.0, "HRKET" to 84.0, "ONRYT" to 68.0, "OZYSR" to 48.0,
            "ALKLC" to 38.0, "YIGIT" to 42.0, "HOROZ" to 68.0, "EFOR" to 24.50, "SEGMN" to 34.0,
            "DCTTR" to 28.0, "BAHKM" to 48.0, "TCKRC" to 68.0, "GUNDG" to 78.0, "AHSGY" to 18.50,
            "OZATD" to 125.0, "CEMZY" to 18.50, "BINBN" to 84.0, "CGCAM" to 48.0, "SMRVA" to 28.0,
            "ARMGD" to 48.0, "DURKN" to 24.0, "EGEGY" to 48.0, "GLRMK" to 68.0, "AKFIS" to 34.0,
            "MOPAS" to 28.0, "SERNT" to 48.0, "BIGEN" to 34.0, "DSTKF" to 48.0, "VSNMD" to 68.0,
            "ENDAE" to 18.50, "KLYPV" to 48.0, "BALSU" to 34.0, "BULGS" to 48.0, "DMLKT" to 28.0,
            "DOFRB" to 38.0, "MARMR" to 48.0, "ECOGR" to 34.0, "VAKFA" to 48.0, "PAHOL" to 68.0
        ))

        // GRUP 16
        addBist("Grup 16", listOf(
            "ZERGY" to 18.50, "ARFYE" to 28.0, "MEYSU" to 16.50, "ZGYO" to 8.40, "FRMPL" to 34.0,
            "UCAYM" to 24.0, "NETCD" to 48.0, "AKHAN" to 28.0, "BESTE" to 34.0, "ATATR" to 48.0,
            "EMPAE" to 68.0, "SVGYO" to 18.50, "GENKM" to 48.0, "LXGYO" to 14.20, "MCARD" to 68.0,
            "ENPRA" to 34.0, "EKDMR" to 48.0, "BETAE" to 28.0, "ORZAX" to 48.0, "SOHOE" to 68.0,
            "GOLDA" to 24.0, "EKIM" to 48.0, "ISVEA" to 34.0, "SSAAT" to 68.0, "SARAE" to 48.0,
            "METEN" to 34.0, "ALBTN" to 68.0, "MASFN" to 48.0, "KARCL" to 34.0, "QUICK" to 68.0,
            "CITAS" to 48.0, "TKNKA" to 34.0, "KPEKS" to 68.0, "VEYAS" to 48.0, "BKRGY" to 28.0,
            "INTET" to 48.0, "NETGL" to 34.0
        ))

        // COIN / KRİPTO GRUBU
        val coins = listOf(
            "ADAUSDT" to 0.72, "SUNUSDT" to 0.024, "GALAUSDT" to 0.038, "DENTUSDT" to 0.0018,
            "ALGOUSDT" to 0.28, "DOGEUSDT" to 0.38, "BANDUSDT" to 1.65, "XLMUSDT" to 0.44,
            "PEPEUSDT" to 0.000018, "FLRUSDT" to 0.028, "SUSDT" to 1.00, "APTUSDT" to 12.80,
            "SHIBUSDT" to 0.000024, "LQTYUSDT" to 1.15, "ARBUSDT" to 0.88, "ISUSDT" to 0.45,
            "APEUSDT" to 1.25, "SPELLUSDT" to 0.00095, "AUDIOUSDT" to 0.18, "STORJUSDT" to 0.65,
            "BATUSDT" to 0.26, "MANAUSDT" to 0.55, "SANDUSDT" to 0.52, "CHZUSDT" to 0.085,
            "TRXUSDT" to 0.21, "AUSDT" to 1.00, "GRTUSDT" to 0.24, "XTZUSDT" to 1.10,
            "SNXUSDT" to 2.15, "ARPAUSDT" to 0.065, "BLURUSDT" to 0.32
        )
        coins.forEach { (pair, basePrice) ->
            list.add(
                StockItem(
                    ticker = pair,
                    fullSymbol = "BINANCE:$pair",
                    name = pair.replace("USDT", " / USDT"),
                    group = "Kripto",
                    sector = "Kripto Varlıklar",
                    basePrice = basePrice,
                    currency = "$"
                )
            )
        }

        // ÖZEL LİSTE & ENDEKSLER
        val indices = listOf(
            "XU100" to 9850.0 to "BIST 100 Endeksi",
            "XU030" to 10800.0 to "BIST 30 Endeksi",
            "XBANK" to 14200.0 to "BIST Banka Endeksi",
            "XUSIN" to 13400.0 to "BIST Sınai Endeksi",
            "XBLSM" to 4800.0 to "BIST Bilişim Endeksi",
            "XELKT" to 6200.0 to "BIST Elektrik Endeksi",
            "XGIDA" to 11200.0 to "BIST Gıda Endeksi",
            "XGMYO" to 3100.0 to "BIST Gayrimenkul YO",
            "XHOLD" to 8900.0 to "BIST Holding Endeksi",
            "XILTM" to 2400.0 to "BIST İletişim Endeksi",
            "XINSA" to 12500.0 to "BIST İnşaat Endeksi",
            "XKAGT" to 4200.0 to "BIST Orman Kağıt",
            "XKMYA" to 12800.0 to "BIST Kimya Petrol Plastik",
            "XMADN" to 6800.0 to "BIST Madencilik",
            "XMANA" to 9400.0 to "BIST Metal Ana",
            "XMESY" to 18500.0 to "BIST Metal Eşya Makine",
            "XSGRT" to 48000.0 to "BIST Sigorta Endeksi",
            "XSPOR" to 4900.0 to "BIST Spor Endeksi",
            "XTAST" to 11200.0 to "BIST Taş Toprak",
            "XTCRT" to 7400.0 to "BIST Ticaret Endeksi",
            "XTEKS" to 3800.0 to "BIST Tekstil Deri",
            "XTRZM" to 1450.0 to "BIST Turizm Endeksi",
            "XTUMY" to 12600.0 to "BIST Tüm-100",
            "XU500" to 11800.0 to "BIST 500 Endeksi",
            "XULAS" to 34500.0 to "BIST Ulaştırma Endeksi",
            "XUTEK" to 16400.0 to "BIST Teknoloji Endeksi",
            "XUTUM" to 11200.0 to "BIST Tüm Endeksi"
        )
        indices.forEach { (pair, name) ->
            val (ticker, basePrice) = pair
            list.add(
                StockItem(
                    ticker = ticker,
                    fullSymbol = "BIST:$ticker",
                    name = name,
                    group = "Endeksler",
                    sector = "Borsa Endeksi",
                    basePrice = basePrice,
                    currency = " puan"
                )
            )
        }

        list
    }

    private fun getCompanyName(ticker: String): String {
        return when (ticker) {
            "THYAO" -> "Türk Hava Yolları A.Ş."
            "ASELS" -> "Aselsan Elektronik Sanayi"
            "GARAN" -> "Garanti BBVA"
            "AKBNK" -> "Akbank T.A.Ş."
            "ISCTR" -> "İş Bankası (C)"
            "YKBNK" -> "Yapı ve Kredi Bankası"
            "EREGL" -> "Ereğli Demir ve Çelik Fabrikaları"
            "KCHOL" -> "Koç Holding A.Ş."
            "SAHOL" -> "Hacı Ömer Sabancı Holding"
            "SISE" -> "Türkiye Şişe ve Cam Fabrikaları"
            "TUPRS" -> "Tüpraş Petrol Rafinerileri"
            "BIMAS" -> "BİM Birleşik Mağazalar"
            "SASA" -> "Sasa Polyester Sanayi"
            "FROTO" -> "Ford Otomotiv Sanayi"
            "TOASO" -> "Tofaş Türk Otomobil Fabrikası"
            "PGSUS" -> "Pegasus Hava Taşımacılığı"
            "TCELL" -> "Turkcell İletişim Hizmetleri"
            "TTKOM" -> "Türk Telekomünikasyon"
            "PETKM" -> "Petkim Petrokimya Holding"
            "ASTOR" -> "Astor Enerji A.Ş."
            "ALARK" -> "Alarko Holding"
            "ENJSA" -> "Enerjisa Enerji"
            "ARCLK" -> "Arçelik A.Ş."
            "MGROS" -> "Migros Ticaret A.Ş."
            "CCOLA" -> "Coca-Cola İçecek"
            "SOKM" -> "Şok Marketler Ticaret"
            "EKGYO" -> "Emlak Konut Gayrimenkul"
            "VESTL" -> "Vestel Elektronik"
            "KOZAL" -> "Koza Altın İşletmeleri"
            "OTKAR" -> "Otokar Otomotiv"
            "TTRAK" -> "Türk Traktör"
            "MIATK" -> "Mia Teknoloji"
            "REEDR" -> "Reeder Teknoloji"
            "EUPWR" -> "Europower Enerji"
            "CWENE" -> "Cw Enerji"
            else -> "$ticker Pay Senedi"
        }
    }

    private fun getSector(ticker: String, defaultSector: String): String {
        return when {
            ticker in listOf("GARAN", "AKBNK", "ISCTR", "YKBNK", "VAKBN", "HALKB", "SKBNK", "ALBRK", "KLNMA") -> "Bankacılık"
            ticker in listOf("THYAO", "PGSUS", "TAVHL", "CLEBI", "AYCES") -> "Havacılık & Ulaştırma"
            ticker in listOf("ASTOR", "ENJSA", "AKSEN", "CWENE", "EUPWR", "GWIND", "AYDEM", "ZOREN", "CANTE") -> "Enerji & Yenilenebilir"
            ticker in listOf("EREGL", "KRDMD", "KRDMA", "KRDMB", "ISDMR", "CEMTS", "BOSSA") -> "Demir Çelik & Metal"
            ticker in listOf("KCHOL", "SAHOL", "AGHOL", "ALARK", "DOHOL", "TKFEN", "GSDHO") -> "Holding & Yatırım"
            ticker in listOf("TUPRS", "PETKM", "SASA", "HEKTS", "GUBRF", "AKSA") -> "Kimya & Petrol"
            ticker in listOf("BIMAS", "MGROS", "SOKM", "BIZIM", "CRFSA") -> "Perakende Ticaret"
            ticker in listOf("TCELL", "TTKOM", "NETAS", "KFEIN", "ARDYZ", "FONET", "MIATK", "LOGO") -> "Bilişim & Telekom"
            ticker in listOf("EKGYO", "SNGYO", "ISGYO", "TRGYO", "KLGYO", "OZKGY") -> "Gayrimenkul Yatırım"
            ticker in listOf("CCOLA", "AEFES", "ULKER", "TATGD", "TUKAS", "PNSUT") -> "Gıda & İçecek"
            ticker in listOf("FROTO", "TOASO", "OTKAR", "TTRAK", "ASUZU", "KARSN") -> "Otomotiv"
            ticker in listOf("ASELS", "SDTTR") -> "Savunma Sanayi"
            else -> defaultSector
        }
    }
}
