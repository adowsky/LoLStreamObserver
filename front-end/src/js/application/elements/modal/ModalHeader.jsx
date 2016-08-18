import React from 'react';
import ButtonGroup from '../ContentTable/components/ButtonGroup.jsx';



export default class ModalHeader extends React.Component {

    constructor(props) {
        super(props);
        this.getButtons = this.getButtons.bind(this);

    }

    getButtons(){
        return[
            {
                name: "Close",
                aClass: "remove-button",
                action: this.props.handlers.close
            }
        ]
    }


    render() {
        return <div className="modal-header side">
            {this.props.children}
            <ButtonGroup buttons={this.getButtons()}/>
        </div>
    }
    }