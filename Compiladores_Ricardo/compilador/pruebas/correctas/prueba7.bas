rem prueba7.bas (correcta)

sub fibonacci(veces)
	static primero
	static segundo
	static auxiliar
	
	let primero = 0
	let segundo = 1

	if (veces > 0) then
		print "0"

	if (veces > 1) then
		print "1"
	
	while (veces > 2)
		print (primero + segundo)		
		
		let auxiliar = primero
		let primero  = segundo
		let segundo  = auxiliar + segundo

		let veces = veces - 1
	wend

end sub

print "Ingrese un numero:"
input veces

print "Fibonacci:"
call fibonacci(veces)

end
