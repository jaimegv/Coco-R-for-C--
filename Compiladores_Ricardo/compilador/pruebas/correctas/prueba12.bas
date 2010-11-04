rem prueba12.bas (correcta)

sub factorial
	print "Ingrese un numero:"
	
	static numero
	input numero
	
	static original
	let original = numero
	
	static resultado
	let resultado = 1

	while (numero > 0)
		let resultado = resultado * numero
		let numero = numero - 1
	wend
	
	print "El factorial de " ; original ; " es " ; resultado
end sub

sub suma
	static izquierda
	static derecha
	
	print "Ingrese un numero:"
	input izquierda
	
	print "Ingrese un numero:"
	input derecha
	
	print "La suma de " ; izquierda ; " + " ; derecha ; " es igual a: " ; (izquierda + derecha)
end sub

sub potencia
	static base
	static exponente
	
	print "Ingrese la base:"
	input base
	
	print "Ingrese el exponente:"
	input exponente
	
	print base ; " elevado a " ; exponente ; " es igual a: " ; (base ^ exponente)
end sub

sub adivinar
	static secreto
	print "Ingrese el numero secreto:"
	input secreto

	static intento
	print "Intente adivinar el numero: "
	input intento

	while (intento <> secreto)
		
		if (secreto > intento) then
			print "El numero ingresado en menor que el secreto"
		
		if (secreto < intento) then
			print "El numero ingresado es mayor que el secreto"
		
		print "Intente adivinar el numero: "
		input intento
	
	wend
	
	print "Enhorabuena, ha acertado!"

end sub

sub comparaciones
	print "Ingrese un numero: "
	input a
	print "Ingrese un numero: "
	input b
	
	if (a < b) then
		print "Comparacion (a < b):  " ; a ; " es menor que " ; b
	else
		print "Comparacion (a < b):  " ; a ; " es mayor o igual que " ; b

	if (a >= b) then
		print "Comparacion (a >= b): " ; a ; " es mayor o igual que " ; b
	else
		print "Comparacion (a >= b): " ; a ; " es menor que " ; b

	if (a > b) then
		print "Comparacion (a > b):  " ; a ; " es mayor que " ; b
	else
		print "Comparacion (a > b):  " ; a ; " es menor o igual que " ; b

	if (a <= b) then
		print "Comparacion (a <= b): " ; a ; " es menor o igual que " ; b
	else
		print "Comparacion (a <= b): " ; a ; " es mayor que " ; b

	if (a = b) then
		print "Comparacion (a = b):  " ; a ; " es igual que " ; b
	else
		print "Comparacion (a = b):  " ; a ; " es distinto que " ; b

	if (a <> b) then
		print "Comparacion (a <> b): " ; a ; " es distinto que " ; b
	else
		print "Comparacion (a <> b): " ; a ; " es igual que " ; b

end sub

sub opcionesNumero
	print ""
	print "Que operacion desea realizar?:"
	print "   1) Factorial"
	print "   2) Suma"
	print "   3) Potencia"
	print "   4) Adivinar numero"
	print "   5) Comparaciones"
	print "   6) Salir"
	print ""
end sub

sub operacionesNumero
	static operacion
	let operacion = 0
	
	call opcionesNumero
	input operacion
	
	while (((operacion < 1) OR (operacion > 6)) OR (operacion <> 6))
		
		if (operacion = 1) then
			call factorial
		
		if (operacion = 2) then
			call suma
		
		if (operacion = 3) then
			call potencia

		if (operacion = 4) then
			call adivinar

		if (operacion = 5) then
			call comparaciones

		call opcionesNumero
		input operacion

	wend

end sub

sub opcionesCadena
	print ""
	print "Que operacion desea realizar?:"
	print "   1) Concatenar"
	print "   2) Concatenar repetitivo"
	print "   3) Salir"
	print ""
end sub

sub concatenar
	static cadena1$
	print  "Ingrese una cadena: "
	input  cadena1$
	
	static cadena2$
	print  "Ingrese otra cadena: "
	input  cadena2$
	
	static resultado$
	let resultado$ = cadena1$ + cadena2$
	
	print "Cadenas concatenadas: " ; resultado$

end sub

sub concatenarRep
	static cadena$
	print  "Ingrese una cadena:"
	input  cadena$
	
	static veces
	print  "Ingrese un numero:"
	input  veces
	
	static resultado$
	
	while (veces > 0)
		let resultado$ = resultado$ + cadena$
		let veces = veces - 1
	wend

	print "La cadena resultante es: " ; resultado$

end sub

sub operacionesCadena
	static operacion
	let operacion = 0
	
	call opcionesCadena
	input operacion
	
	while (((operacion < 1) OR (operacion > 3)) OR (operacion <> 3))
		
		if (operacion = 1) then
			call concatenar
		
		if (operacion = 2) then
			call concatenarRep

		call opcionesCadena
		input operacion

	wend
end sub

sub tiposPruebas
	print ""
	print "Que prueba desea realizar?: "
	print "   1) Con numeros"
	print "   2) Con cadenas"
	print "   3) Salir"
	print ""
end sub

print "Cual es su nombre?: "
input nombre$

print ""
print "Hola, " ; nombre$ ; ". Bienvenido al programa de prueba!"

let tipo = 0
call tiposPruebas
input tipo

while (((tipo < 1) OR (tipo > 3)) OR (tipo <> 3))
	
	if (tipo = 1) then
		call operacionesNumero
	
	if (tipo = 2) then
		call operacionesCadena

	call tiposPruebas
	input tipo

wend

print "Hasta luego, " ; nombre$ ; "!!!"

end
