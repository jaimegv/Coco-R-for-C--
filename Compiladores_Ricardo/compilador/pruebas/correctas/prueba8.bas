rem prueba8.bas (correcta)

sub factorial(numero)
	if (numero > 1) then
		let resultado = resultado * numero
	
	if (numero > 1) then
		call factorial(numero - 1)
end sub

print "Ingrese un numero:"
input valor

let resultado = 1
call factorial(valor)
print "El factorial de " ; valor ; " es: " ; resultado

end
