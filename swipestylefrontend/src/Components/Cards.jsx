/* eslint-disable no-unused-vars */
import {motion} from "framer-motion";
const Cards = ({clothingData}) => {
    console.log(clothingData)
    const {
        altText,
        gender,
        imageUrl,
        name,
        price,
        productId,
        productUrl
    } = clothingData;
    return (
            <motion.img src={imageUrl} alt={altText} className="rounded-lg h-96 w-72 hover:cursor-grab active:cursor-grabbing" 
            style={{
                gridRow:1,
                gridColumn:1
            }}
            drag="x"
            dragConstraints={{
                left:0,
                right:0
            }}
            />
    )
}
export default Cards;