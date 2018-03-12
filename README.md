# PROGETTO COMPILATORI E INTERPRETI 2016/2017

Il progetto consiste nella realizzazione di un compilatore per il linguaggio la cui sintassi e` definita nel file FOOL.g4 

**IL LINGUAGGIO**

Questo linguaggio è una estensione object-oriented del linguaggio funzionale visto a 
lezione. In particolare 

* E' possibile dichiarare classi e sottoclassi. 

* Gli oggetti, che nascono come istanza di classi, contengono campi 
  (dichiarati nella classe o ereditati dalla super-classe) e metodi (esplicitamente 
  dichiarati nella classe o ereditati dalla super-classe). 
  Se in una sottoclasse viene dichiarato un campo o un metodo con il medesimo nome 
  di un campo della super-classe, tale campo o metodo sovrascrive quello della 
  super-classe. 
* I campi non sono modificabili ed il loro valore viene definito quando l'oggetto
  è creato.
* E' inoltre possibile dichiarare funzioni annidate. Le funzioni NON possono 
  essere passate come parametri.

**IL TYPE-CHECKER**

Il compilatore deve comprendere un type-checker che controlli il corretto uso dei tipi. 

* Si deve considerare una nozione di subtyping fra classi e tipi di funzioni. 

*Il tipo di una funzione f1 è sottotipo del tipo di una funzione f2 se il tipo ritornato da f1 
  è sottotipo del tipo ritornato da f2, se hanno il medesimo numero di parametri, e se  
  ogni tipo di paramentro di f1 è sopratipo del corrisponde tipo di parametro di f2.* 
  
  *Una classe C1 è sottotipo di una classe C2 se C1 estende C2 e se i campi e metodi che 
  vengono sovrascritti sono sottotipi rispetto ai campi e metodi corrispondenti di C2.*
   
  *Inoltre, C1 è sottotipo di C2 se esiste una classe C3 sottotipo di C2 di cui C1 è
  sottotipo.*

**IL CODICE OGGETTO**

Il compilatore deve generare codice per un esecutore virtuale chiamato SVM (stack 
virtual machine) la cui sintassi è definita nel file SVM.g. Tale esecutore ha una 
memoria in cui gli indirizzi alti sono usati per uno stack. Uno stack pointer punta alla 
locazione successiva alla prossima locazione libera per lo stack (se la memoria ha 
indirizzi da 0 a MEMSIZE-1, lo stack pointer inizialmente punta a MEMSIZE). 
In questo modo, quando lo stack non e` vuoto, lo stack pointer punta al top dello stack. 

Il programma e` collocato in una memoria separata puntata dall' instruction pointer 
(che punta alla prossima istruzione da eseguire). Gli altri registri della macchina 
virtuale sono: HP (heap pointer), RA (return address), RV (return value) e FP 
(frame pointer). 
In particolare, HP serve per puntare alla prossima locazione disponibile dello 
heap; assumendo di usare gli indirizzi bassi per lo heap, HP contiene inizialmente 
il valore 0.

**OPZIONALI**

Le seguenti richieste non sono obbligatorie:

-- Estensione del codice visto a lezione con operatori "<=", ">=", "||", "&&", "/", "-" 
   e "not"

-- La deallocazione degli oggetti nello heap (garbage collection) NON E` OBBLIGATORIA.
   Chi è interessato può scrivere il modulo relativo.
  
# Installare e usare ANTLR 
Dal seguente link --> https://github.com/jknack/antlr4ide: 
seguire la sezione **Installazione** fino al punto 4 (il file *antlr-4.6-complete.jar* è già inglobato nella cartella lib del progetto clonato)

**NOTA:** 
* Windows > Show View > Other.. > ANTLR > aggiungere Parse Tree e Syntax Diagram
   
# Clonare il progetto
- `git clone https://github.com/alciarro93/compilatori-interpreti.git`
-  Importare il progetto in Eclipse 
-  Andare nella sezione "Lanciare un esempio per parsing/analysis" 

# Importare il progetto (ex-novo): 
* Scaricare dalla piattaforma e-learning il codice sorgente del progetto
* Scaricare il file `antlr-4.6-complete.jar` dal sito http://www.antlr.org/download/index.html (NON L'ULTIMA VERSIONE - MOLTE FUNZIONI SONO DEPRRECATE) 

1. Crea un nuovo progetto Java (File/New/Java Project) 

	a. dargli un nome (SENZA SUFFISSI) ESEMPIO Compilatori
	
	b. togiere il checkbox "Use default location"
	
	c. fare "Browse..." e selezionare la cartella "Java sources"
	
	d. premere su "New Folder" (chiamare il Folder col nome del progetto)
	
	e. premere "Finish"

2. Estrarre la cartella scaricata e sostituirla alla cartella "src" del progetto "Compilatori" appena creato. 

3. Andare sul progetto "Compilatori" e click destro/Properties/ANTLR4/Tool

	a. Cliccare su "Enable project specific settings"
	
	b. in "Options" scrivere "./src" (SENZA DOPPI APICI)
	
	c. rimuovere lo spunto da "Generate a parse tree listener"
	
	d. fare "Apply" e "OK"

4. Posizionarsi nella cartella "lib"

	a. copiare e incollare in questa cartella il file antlr-4.6-complete.jar
	
	b. cliccare col tasto destro sul jar appena incollato
	
	c. cliccare su Build Path > add to Build Path

# Lanciare un esempio per parsing/analysis

1. Dalla finestra "Parse Tree" (se non si dispone vedere la nota della sezione "Installare e usare ANTLR"),fare paste di un file da analizzare come ad esempio: `let int x = 3; in print(x+2);` 
	
**NOTA**: Per risolvere eventuali conflitti: Tasto destro > Proprieties > ANTLR4 > Tool > Add > Selezionare il jar aggiunto (4.6) deselezionare la versione di defuault 4.4 facendo attenzione al percorso in cui è contenuta la versione 4.6. Nel caso aggiungere il corretto percorso che si trova nella cartella `lib`.

**NOTA2**: Si lavorerà con i visitor anzichè che con i listener per questo andare sul file.g4 > tasto dx > Run As > External tool Configuration > cambiare da `-listener -no-visitor -encoding UTF-8` a `-listener -visitor -encoding UTF-8`. Se non compaiono i file, allora andare nelle proprietà del progetto ANTLR4 > Tool > Enable project Specific settings > Sputare la casella `Generate parse tree visitorss (-visitors)` 
