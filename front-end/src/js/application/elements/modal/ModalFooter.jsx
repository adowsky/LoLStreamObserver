import React from 'react';
import ButtonGroup from '../ContentTable/components/ButtonGroup.jsx';



export default class ModalFooter extends React.Component {

    constructor(props) {
        super(props);
        this.getButtons = this.getButtons.bind(this);

    }

    getButtons(){
        return[
            {
                name: "Apply",
                aClass: "apply-button",
                action: this.props.handlers.apply
            }
        ]
    }


    render() {
        return <div className="modal-footer side">
            <ButtonGroup buttons={this.getButtons()}/>
        </div>
    }
}