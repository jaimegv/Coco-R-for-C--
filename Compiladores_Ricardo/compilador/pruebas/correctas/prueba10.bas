rem prueba10.bas (correcta)

sub resto(a, b)
	static resto
	let resto = a

	while (resto >= b)
		let resto = resto - b
	wend

	let temporal = resto
end sub

sub primo(numero)
	static divide
	let divide = 0
	
	static actual
	let actual = numero - 1
	
	while (actual > 1)
		call resto(numero, actual)
		
		if (temporal = 0) then
			let divide = divide + 1
		
		let actual = actual - 1
	wend
	
	if (divide > 0) then
		print "El numero " ; numero ; " no es primo"
	else
		print "El numero " ; numero ; " es primo"

end sub

print "Ingrese un numero:"
input valor

let temporal = 0
call primo(valor)

end
